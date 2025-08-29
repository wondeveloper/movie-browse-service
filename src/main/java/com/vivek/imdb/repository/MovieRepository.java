package com.vivek.imdb.repository;

import com.vivek.imdb.entity.Movie;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface MovieRepository extends ReactiveCrudRepository<Movie, String> {


    @Modifying
    @Query("DELETE FROM movie WHERE id = :id")
    Mono<Integer> deleteMovieById(String id);
}
