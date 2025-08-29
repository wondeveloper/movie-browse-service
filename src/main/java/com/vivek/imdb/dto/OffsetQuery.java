package com.vivek.imdb.dto;

import org.springframework.data.domain.Sort;

public record OffsetQuery(
        Integer page,
        Integer size,
        SortSpec sort
) implements MovieQuery {
    public OffsetQuery {
        // Apply defaults if needed
        if(page == null) page = 0;
        if (size == null || size <= 0) size = 20;
        if (sort == null) sort = SortSpec.sort(Sort.by(Sort.Order.asc("createdAt")));
    }
}
