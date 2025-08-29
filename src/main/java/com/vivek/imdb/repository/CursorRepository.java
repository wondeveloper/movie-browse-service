package com.vivek.imdb.repository;

import com.vivek.imdb.entity.Movie;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface CursorRepository extends R2dbcRepository<Movie,String> {

    Flux<Movie> findAllBy(Pageable pageable);

}
