package com.vivek.imdb.dto;

import jakarta.annotation.Nullable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record SortSpec(List<OrderSpec> orders) {

    public static SortSpec sort(@Nullable Sort sort){
        if (sort == null || sort.isUnsorted()) return new SortSpec(Collections.emptyList());
        List<OrderSpec> orderSpecs = new ArrayList<>();
        sort.forEach(p -> orderSpecs.add(new OrderSpec(p.getProperty(), p.getDirection().isAscending())));
        return new SortSpec(orderSpecs);
    }

    public Sort toSortOrUnSorted(){
        if (orders == null || orders.isEmpty()) return Sort.unsorted();
        return Sort.by(
                orders.stream()
                        .map(orderSpec -> orderSpec.asc() ? Sort.Order.asc(orderSpec.property())
                                : Sort.Order.desc(orderSpec.property()))
                        .toList());
    }
}
