package com.vivek.imdb.dto;

public enum PagingMode {
    SEEK_CURSOR,
    SEEK_CURSOR_SORT,//server will provide the next cursor for next page
    OFFSET, //
    OFFSET_WITH_COUNT
}
