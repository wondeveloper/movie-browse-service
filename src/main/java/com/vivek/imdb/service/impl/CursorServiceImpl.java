package com.vivek.imdb.service.impl;

import com.vivek.imdb.config.OffsetToken;
import com.vivek.imdb.config.SeekToken;
import com.vivek.imdb.config.TokenPayload;
import com.vivek.imdb.dto.*;
import com.vivek.imdb.entity.Movie;
import com.vivek.imdb.service.MoviePaginationService;
import com.vivek.imdb.repository.PaginationAndSearchingRepository;
import com.vivek.imdb.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.*;

import static com.vivek.imdb.util.CursorUtil.*;

@Service("seekMovieService")
@RequiredArgsConstructor
public class CursorServiceImpl implements MoviePaginationService<OffsetToken> {

    private final PaginationAndSearchingRepository paginationRepo;

    private final R2dbcEntityTemplate template;

    @Override
    public Mono<CursorPage<MovieDetails, OffsetToken>> fetchMovies(Mono<MovieQueryDto> queryDtoMono) {
        return queryDtoMono
                .transform(RequestValidator.validateClientCursorV2()) // assume this converts DTO -> MovieQuery
                .flatMap(this::dispatch); // keep everything non-blocking
    }

    private Mono<CursorPage<MovieDetails, OffsetToken>> dispatch(MovieQuery mquery) {
        return switch (mquery) {
            case OffsetQuery q -> handleOffset(q);
            case CursorQuery q -> handleSeek(q);
            default -> Mono.error(new IllegalArgumentException("Unsupported query type: " + mquery.getClass().getSimpleName()));
        };
    }

    private Mono<CursorPage<MovieDetails, OffsetToken>> handleOffset(OffsetQuery q) {
        final Sort sort = q.sort().toSortOrUnSorted();
        final int size = PagingDefaults.sizeOrDefault(q.size());
        final int page = PagingDefaults.pageOrDefault(q.page());

        // Correct order: (page, size, sort). Use size+1 to probe for hasMore.
        final PageRequest pageable = PageRequest.of(page, size + 1, sort);

        return Mono.zip(
                        paginationRepo.findAllBy(pageable).collectList(),
                        paginationRepo.count()
                )
                .map(tuple -> buildPageFromOffset(tuple.getT1(), tuple.getT2(), sort, size, page));
    }

    private Mono<CursorPage<MovieDetails, OffsetToken>> handleSeek(CursorQuery q) {
        final TokenPayload payload = decodePayloadCursor(q.cursorB64());
        if (!(payload instanceof SeekToken seekToken)) {
            return Mono.error(new IllegalArgumentException("Invalid cursor payload type"));
        }

        final Sort sort = seekToken.sort().toSortOrUnSorted();
        final int size =  PagingDefaults.sizeOrDefault(seekToken.size());
        //final int nextPage = seekToken.nextPageNumber();
        final PageRequest pageable = PageRequest.of(0, size + 1, sort); // 0 since we need first page after sorting
        Map<String,String> map = seekToken.prev();
        Instant createdAfter = map.get("createdAt") != null ? Instant.parse(map.get("createdAt")) : null;
        Mono<List<Movie>> movies;
        if (null != createdAfter && !seekToken.lastId().isEmpty()){
            movies = paginationRepo.findByCreatedAtAfterOrCreatedAtIsAndIdGreaterThan(createdAfter, createdAfter,seekToken.lastId(), pageable).collectList();

        }else if (null != createdAfter){
            movies = paginationRepo.findByCreatedAtAfterOrCreatedAtIs(createdAfter, createdAfter, pageable).collectList();

        } else {
            movies = paginationRepo.findAllBy(pageable).collectList();
        }
        return Mono.zip(
                        movies,
                        paginationRepo.count()
                )
                .map(tuple -> buildPageFromSeek(tuple.getT1(), tuple.getT2(), seekToken));
    }

    private Mono<CursorPage<MovieDetails, OffsetToken>> handleSeek2(CursorQuery q) {
        final TokenPayload payload = decodePayloadCursor(q.cursorB64());
        if (!(payload instanceof SeekToken seekToken)) {
            return Mono.error(new IllegalArgumentException("Invalid cursor payload type"));
        }

        final Sort resolvedSort = Optional.ofNullable(seekToken.sort())
                .map(SortSpec::toSortOrUnSorted)
                .filter(Sort::isSorted)
                .orElseGet(() -> Sort.by(Sort.Order.asc("createdAt"), Sort.Order.asc("id")));
        final List<Sort.Order> orders = resolvedSort.stream().toList();
        if (orders.isEmpty()) {
            return Mono.error(new IllegalArgumentException("Seek paging requires a stable Sort"));
        }
        final int size = Math.max(1, seekToken.size());
        //final int nextPage = seekToken.nextPageNumber();
        //final PageRequest pageable = PageRequest.of(nextPage, size + 1, sort);
        Map<String,String> lastKeys = Optional.ofNullable(seekToken.prev()).orElse(Map.of());
        Criteria where = lastKeys.isEmpty() ? null : buildKeysetCriteria(orders, lastKeys);
        Query query = where == null ? Query.empty() : Query.query(where).sort(Sort.by(orders)).limit(size+1);
//        List<Criteria> criteria = map.entrySet().stream().map(p -> Criteria.where(p.getKey()).greaterThan(p.getValue()))
//                .toList();
//        List<Sort.Order> sorts = seekToken.sort().orders().stream().map(p -> p.asc() ? Sort.Order.asc(p.property())
//                :  Sort.Order.desc(p.property())).toList();
//        Query query = Query.query(Criteria.from(criteria)).sort(Sort.by(sorts)).limit(size+1);
        Mono<List<Movie>> movieFlux = template.select(query, Movie.class).collectList();
        return Mono.zip(
                        movieFlux,
                        template.count(Query.empty(), Movie.class)
                )
                .map(tuple -> buildPageFromSeek(tuple.getT1(), tuple.getT2(), seekToken));
    }

    private Criteria buildKeysetCriteria(List<Sort.Order> orders, Map<String, String> keys) {
        Criteria chain = null;
        List<Criteria> equalsSoFar = new ArrayList<>();
        for (Sort.Order order : orders){
            String property = order.getProperty();
            if (!keys.containsKey(property)) {
                throw new IllegalArgumentException("Missing key in cursor: " + property);
            }
            Object obj = keys.get(property);
            Criteria prefix = equalsSoFar.stream().reduce(Criteria.empty(), Criteria::and);
            Criteria criteria = order.isAscending()
                    ? Criteria.where(property).greaterThan(obj)
                    : Criteria.where(property).lessThan(obj);
            Criteria disjunct = prefix.isEmpty() ? criteria : prefix.and(criteria);
            chain = (chain == null) ? disjunct : chain.or(disjunct);
            equalsSoFar.add(Criteria.where(property).is(obj));
        }
        return chain;
    }

    private CursorPage<MovieDetails, OffsetToken> buildPageFromOffset(
            List<Movie> fetched, long totalCount, Sort sort, int size, int page
    ) {
        final boolean hasMore = fetched.size() > size;
        final List<Movie> pageSlice = hasMore ? fetched.subList(0, size) : fetched;

        // If you're intentionally carrying an OffsetToken inside a SeekToken (hybrid),
        // keep it consistent; otherwise consider changing the generic to TokenPayload.
        String lastId = pageSlice.isEmpty() ? "" : pageSlice.getLast() == null ? "" : pageSlice.getLast().getId();
        final SeekToken nextSeekToken = new SeekToken(
                "V1",
                SortSpec.sort(sort),
                //nextCursor,        // note: this is an encoded *offset* token per your original design
                size,
                page + 1,
                totalCount,
                PagingMode.OFFSET,
                Map.of(),
                lastId
        );

        final String nextCursor = hasMore ? encodePayloadCursor(nextSeekToken) : null;
        final OffsetToken nextOffsetToken = new OffsetToken("V1", SortSpec.sort(sort), size, page + 1, nextCursor);
        final List<MovieDetails> items = pageSlice.stream()
                .map(EntityMapper::convertToMovieDetails)
                .toList();

        return new CursorPage<>(items, nextOffsetToken);
    }

    private CursorPage<MovieDetails, OffsetToken> buildPageFromSeek(
            List<Movie> fetched, long totalCount, SeekToken current
    ) {
        final int size = current.size();
        final boolean hasMore = fetched.size() > size;
        final List<Movie> pageSlice = hasMore ? fetched.subList(0, size) : fetched;
        // Preserve your “hybrid” behavior: embed an offset token as nextCursor.

        var last = pageSlice.getLast();
        String lastId = pageSlice.isEmpty() ? "" : pageSlice.getLast() == null ? "" : pageSlice.getLast().getId();

        BeanWrapper wrapper = new BeanWrapperImpl(last);

        Map<String, String> map = current.sort().orders().stream()
                .map(OrderSpec::property)
                .filter(p -> p != null && !p.isBlank())
                .filter(wrapper::isReadableProperty)   // avoid NotReadablePropertyException
                .collect(
                        LinkedHashMap::new,
                        (m, prop) -> m.put(prop, String.valueOf(wrapper.getPropertyValue(prop))), // allows nulls
                        Map::putAll
                );

        assert lastId != null;
        final SeekToken nextSeekToken = new SeekToken(
                "V1",
                SortSpec.sort(current.sort().toSortOrUnSorted()),
                //nextOffsetCursor,              // you were storing the *offset* cursor here
                size,
                current.nextPageNumber() + 1,
                totalCount,
                PagingMode.SEEK_CURSOR,
                map,
                lastId
        );

        final String nextOffsetCursor = hasMore ? encodePayloadCursor(nextSeekToken) : null;

        final OffsetToken nextOffsetToken = new OffsetToken(
                "V1",
                SortSpec.sort(current.sort().toSortOrUnSorted()),
                size,
                current.nextPageNumber() + 1,
                nextOffsetCursor
                );

        final List<MovieDetails> items = pageSlice.stream()
                .map(EntityMapper::convertToMovieDetails)
                .toList();

        return new CursorPage<>(items, nextOffsetToken);
    }

}
