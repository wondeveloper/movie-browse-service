package com.vivek.imdb.dto;

import jakarta.annotation.Nullable;
import reactor.core.publisher.Mono;

public record MovieQueryDto(
        PagingMode pagingMode,
        @Nullable String cursorB64,
        Integer page,
        @Nullable Integer size,
        @Nullable SortSpec sort
) {

    public static Mono<MovieQueryDto> seek(@Nullable String cursorB64, Integer size){
        return Mono.just(new MovieQueryDto(PagingMode.SEEK_CURSOR, cursorB64, size, null, null));
    }

    public static Mono<MovieQueryDto> seekSort(@Nullable String cursorB64, Integer size, @Nullable SortSpec sort){
        return Mono.just(new MovieQueryDto(PagingMode.SEEK_CURSOR_SORT, cursorB64, size, null, sort));
    }

    public static Mono<MovieQueryDto> offset(Integer page, Integer size, @Nullable SortSpec sort){
        return Mono.just(new MovieQueryDto(PagingMode.OFFSET, null, page, size , sort));
    }

    public static Mono<MovieQueryDto> offsetCount(Integer page, Integer size, @Nullable SortSpec sort){
        return Mono.just(new MovieQueryDto(PagingMode.OFFSET_WITH_COUNT, null, page, size , sort));
    }


}
