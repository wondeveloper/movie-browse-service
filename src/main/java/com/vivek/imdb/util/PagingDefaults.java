package com.vivek.imdb.util;

import com.vivek.imdb.dto.SortSpec;
import org.springframework.data.domain.Sort;

public final class PagingDefaults {

    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 20;
    public static final SortSpec DEFAULT_SORT = SortSpec.sort(Sort.by(Sort.Order.asc("createdAt"), Sort.Order.asc("id")));

    public static int sizeOrDefault(Integer size) {
        return (size == null || size <= 0) ? DEFAULT_SIZE : size;
    }
    public static int pageOrDefault(Integer page) {
        return (page == null || page < 0) ? DEFAULT_PAGE : page;
    }
    public static SortSpec sortOrDefault(SortSpec sort) {
        return (sort == null) ? DEFAULT_SORT : sort;
    }
}
