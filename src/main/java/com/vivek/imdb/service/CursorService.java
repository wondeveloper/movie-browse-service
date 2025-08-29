package com.vivek.imdb.service;

import com.vivek.imdb.dto.CursorPage;
import com.vivek.imdb.entity.Movie;
import com.vivek.imdb.util.SeekToken;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Mono;

import java.util.Collections;

public interface CursorService {

    //this style is better for legacy code
    default Mono<CursorPage<Movie, SeekToken>> fetchMovie(@Nullable String cursorB64, Integer size, Integer pageNumber, Sort sort){
        if (pageNumber == null || sort == null){
            return fetchMovie(cursorB64, size);
        }
        return Mono.just(new CursorPage<>(Collections.emptyList(), null));
    };

    Mono<CursorPage<Movie,SeekToken>> fetchMovie(@Nullable String cursorB64, int size);
}
