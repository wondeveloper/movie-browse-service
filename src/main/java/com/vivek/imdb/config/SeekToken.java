package com.vivek.imdb.config;

import com.vivek.imdb.dto.PagingMode;
import com.vivek.imdb.dto.SortSpec;

import java.util.Map;

public record SeekToken(
        String version,
        SortSpec sort,
        //String encodedCursor,
        int size,
        int nextPageNumber,
        Long totalSize,
        PagingMode mode,
        Map<String,String> prev,
        String lastId

) implements TokenPayload {
    public SeekToken {

    }
}
