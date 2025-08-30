package com.vivek.imdb.service.impl;

import com.vivek.imdb.dto.*;
import com.vivek.imdb.entity.Movie;
import com.vivek.imdb.service.MoviePaginationService;
import com.vivek.imdb.repository.PaginationAndSearchingRepository;
import com.vivek.imdb.util.CursorUtil;
import com.vivek.imdb.util.EntityMapper;
import com.vivek.imdb.config.OffsetToken;
import com.vivek.imdb.config.SeekToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service("offsetMovieService")
public class OffsetMovieService implements MoviePaginationService<OffsetToken> {

    @Autowired
    private PaginationAndSearchingRepository paginationAndSearchingRepository;

    @Override
    public Mono<CursorPage<MovieDetails, OffsetToken>> fetchMovies(Mono<MovieQueryDto> queryIn) {
        return queryIn.defaultIfEmpty(CursorUtil.createDefaultMovieQuery())
                .map(query -> {
                    int page = query.page() == null ? 0 : Math.max(0, query.page());
                    int size = query.size() == null ? 20 : Math.max(1, Math.min(query.size(),200));
                    //Sort sort = query.sort() == null ? Sort.by("createdAt").ascending(): query.sort();
                    Sort sort = query.sort() == null ? Sort.by(Sort.Order.asc("createdAt"), Sort.Order.asc("id")) : query.sort().toSortOrUnSorted();
                    return new ResolvedQuery(page, size, sort, query);
                })
                .flatMap(rq -> {
                    var pageable = PageRequest.of(rq.page, rq.size, rq.sort);
                    Mono<List<Movie>> movieFlux = paginationAndSearchingRepository.findAllBy(pageable).collectList();
                    Mono<Long> count = paginationAndSearchingRepository.count();
                    return Mono.zip(movieFlux, count)
                            .map(tuple -> {
                                var list = tuple.getT1();
                                List<MovieDetails> movieDetailsList = list.stream().map(EntityMapper::convertToMovieDetails).toList();
                                MovieDetails lastDetails = movieDetailsList.getLast() == null ? null : movieDetailsList.getLast();
                                var totalCount = tuple.getT2();
                                int lastPage = (int) Math.max(0, (totalCount + rq.size - 1) / rq.size - 1);

                                final SeekToken nextSeekToken = new SeekToken(
                                        "V1",
                                        SortSpec.sort(rq.sort),
                                       // encodedCursor,
                                        rq.size,
                                        rq.page + 1,
                                        totalCount,
                                        PagingMode.OFFSET,
                                        Map.of(),
                                        lastDetails == null ? "" : lastDetails.id()
                                );

                                String encodedCursor = rq.page < lastPage
                                        ? CursorUtil.encodePayloadCursor(nextSeekToken) : null;
                                final OffsetToken nextOffsetToken = new OffsetToken(
                                        "V1",
                                        SortSpec.sort(rq.sort),
                                        rq.size,
                                        rq.page + 1,
                                        encodedCursor
                                );

                                return new CursorPage<>(movieDetailsList, nextOffsetToken);
                            });
                });
    }

    private record ResolvedQuery(int page, int size, Sort sort, MovieQueryDto original) {}

}
