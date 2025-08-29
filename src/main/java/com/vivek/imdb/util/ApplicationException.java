package com.vivek.imdb.util;

import com.vivek.imdb.exceptions.EmptyTitleException;
import com.vivek.imdb.exceptions.InvalidYearException;
import com.vivek.imdb.exceptions.MovieNotFoundException;
import reactor.core.publisher.Mono;

public class ApplicationException {

    public static <T> Mono<T> invalidYear(){
        return Mono.error(new InvalidYearException("Invalid Year"));
    }

    public static <T> Mono<T> invalidTitle(){
        return Mono.error(new EmptyTitleException("Title should not be empty"));
    }

    public static <T> Mono<T> MovieNotFound(String id){
        return Mono.error(new MovieNotFoundException("Movie %s is not found".formatted(id)));
    }
}
