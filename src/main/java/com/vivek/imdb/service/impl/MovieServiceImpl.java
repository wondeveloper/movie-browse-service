package com.vivek.imdb.service.impl;

import com.vivek.imdb.dto.MovieDetails;
import com.vivek.imdb.dto.MovieDto;
import com.vivek.imdb.repository.MovieRepository;
import com.vivek.imdb.service.MovieService;
import com.vivek.imdb.util.EntityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class MovieServiceImpl implements MovieService {

    @Autowired
    private MovieRepository movieRepository;

    @Override
    public Mono<MovieDetails> addNewMovie(Mono<MovieDto> movieDto) {
        return  movieDto
                .map(EntityMapper::convertToMovie)
                .flatMap(this.movieRepository::save)
                .map(EntityMapper::convertToMovieDetails);
    }

    @Override
    public Mono<MovieDetails> updateMovie(String id, Mono<MovieDto> movieDto) {
        return this.movieRepository.findById(id)
                .switchIfEmpty(Mono.empty())
                .flatMap(m -> movieDto)
                .map(EntityMapper::convertToMovie)
                .doOnNext(movie -> movie.setId(id))
                .doOnNext(movie -> movie.setNew(false))
                .flatMap(this.movieRepository::save)
                .map(EntityMapper::convertToMovieDetails);
    }

    @Override
    public Mono<Boolean> deleteMovie(String id) {
        return this.movieRepository.deleteMovieById(id).hasElement();
    }

    @Override
    public Mono<MovieDetails> getMovie(String id) {
        return this.movieRepository.findById(id)
                .switchIfEmpty(Mono.empty())
                .map(EntityMapper::convertToMovieDetails);
    }
}
