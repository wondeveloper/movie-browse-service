package com.vivek.imdb.controller;

import com.vivek.imdb.dto.*;
import com.vivek.imdb.exceptions.MovieNotFoundException;
import com.vivek.imdb.service.MoviePaginationService;
import com.vivek.imdb.service.MovieService;
import com.vivek.imdb.util.*;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("movie")
@Validated
//@RequiredArgsConstructor
public class MovieController {

    @Autowired
    private MovieService movieService;

    @Autowired
    @Qualifier("offsetMovieService")
    private MoviePaginationService<OffsetToken> offsetMovieService;

    @Autowired
    @Qualifier("seekMovieService")
    private MoviePaginationService<OffsetToken> seekService;


    @PostMapping("add")
    public Mono<MovieDetails> createEntry(@RequestBody Mono<MovieDto> movieDto){
        return movieDto
                .transform(RequestValidator.validate())
                .as(this.movieService::addNewMovie);
    }

    @GetMapping("{id}")
    public Mono<MovieDetails> getMovie(@PathVariable String id){
        return this.movieService.getMovie(id)
                .switchIfEmpty(ApplicationException.MovieNotFound(id));
    }

    @PutMapping("update/{id}")
    public Mono<MovieDetails> updateMovie(@PathVariable String id, @RequestBody Mono<MovieDto> movieDto){
        return movieDto.transform(RequestValidator.validate())
                .as(req -> this.movieService.updateMovie(id, movieDto));
    }

    @DeleteMapping("{id}")
    public Mono<Void> deleteMovie(@PathVariable String id){
        return this.movieService.deleteMovie(id)
                .filter(b -> b)
                .switchIfEmpty(Mono.error(() -> new MovieNotFoundException("Movie can't be deleted because its not found")))
                .then();

    }

    @GetMapping("page/offset")
    public Mono<CursorPage<MovieDetails, OffsetToken>> fetchNextPage(@RequestParam @Min(0) int page,
                                                                   @RequestParam @Min(0) int size,
                                                                   @RequestParam(required = false) @ValidSort String sort){
        List<OrderSpec> orderSpecs = StringToOrderSpec.toOrderSpecList().apply(sort);
        SortSpec sortSpec = new SortSpec(orderSpecs);
        Mono<MovieQueryDto> movieQueryDto =  MovieQueryDto.offset(size, page, sortSpec);
        return offsetMovieService.fetchMovies(movieQueryDto)
                .switchIfEmpty(Mono.error(new RuntimeException("Something went wrong while fetching pages through offset")));
    }

    @GetMapping("page/seek")
    public Mono<CursorPage<MovieDetails, OffsetToken>> fetchNextPage(@RequestParam @Min(0) int size,
                                                                   @RequestParam(required = false) String cursorB64){
        Mono<MovieQueryDto> movieQueryDto =  MovieQueryDto.seek(cursorB64, size);
        return seekService.fetchMovies(movieQueryDto)
                .switchIfEmpty(Mono.error(new RuntimeException("Something went wrong while fetching pages through seek")));
    }
}
