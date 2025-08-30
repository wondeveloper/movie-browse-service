package com.vivek.imdb.config;

import com.vivek.imdb.dto.SortSpec;

public sealed interface TokenPayload permits OffsetToken, SeekToken {
    String version();
    SortSpec sort();
}
