package com.vivek.imdb.service;

import com.vivek.imdb.dto.MovieDetails;
import com.vivek.imdb.dto.MovieDto;
import reactor.core.publisher.Mono;

public interface MovieService {

    Mono<MovieDetails> addNewMovie(Mono<MovieDto> movieDto);

    Mono<MovieDetails> updateMovie(String id, Mono<MovieDto> movieDto);

    Mono<Boolean> deleteMovie(String id);

    Mono<MovieDetails> getMovie(String id);

    Mono<MovieDetails> findMovieByTitleAndYear(String title, String releaseYear);
}
