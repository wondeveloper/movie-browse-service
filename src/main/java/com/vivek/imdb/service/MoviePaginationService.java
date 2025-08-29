package com.vivek.imdb.service;

import com.vivek.imdb.dto.CursorPage;
import com.vivek.imdb.dto.MovieDetails;
import com.vivek.imdb.dto.MovieQueryDto;
import reactor.core.publisher.Mono;

public interface MoviePaginationService<T> {

    Mono<CursorPage<MovieDetails, T>> fetchMovies(Mono<MovieQueryDto> query);
}
