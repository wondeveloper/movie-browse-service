package com.vivek.imdb.repository;

import com.vivek.imdb.entity.Movie;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;

import java.time.Instant;

public interface PaginationAndSearchingRepository extends CursorRepository {

    Flux<Movie> findAllByCreatedAtAfterOrCreatedAtEquals(Instant after, Instant equal, Pageable pageable);

    Flux<Movie> findByCreatedAtAfterOrCreatedAtIsAndIdGreaterThan(
            Instant lastCreatedAt,
            Instant lastCreatedAtEq,
            String lastId,
            Pageable pageable
    );

    Flux<Movie> findAllBy(Pageable pageable);
}
