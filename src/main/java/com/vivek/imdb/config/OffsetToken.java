package com.vivek.imdb.config;

import com.vivek.imdb.dto.SortSpec;

public record OffsetToken(
        String version,
        SortSpec sort,
        int size,
        int nextPage,
        String encodedCursor
) implements TokenPayload {
    public OffsetToken {

    }
}
