package com.vivek.imdb.util;

import com.vivek.imdb.dto.OrderSpec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class StringToOrderSpec {

    public static List<OrderSpec> toOrderSpecList(String orders){
        if (orders == null || orders.isEmpty()) return Collections.emptyList();
        List<OrderSpec> orderSpecs = new ArrayList<>();
        String[] parts = orders.split(",");
        for (int i = 0; i < parts.length; i += 2) {
            String field = parts[i].trim();
            String dir = parts[i+1].trim();
            orderSpecs.add(new OrderSpec(field, dir.equals("asc")));
        }
        return orderSpecs;
    }

    public static Function<String, List<OrderSpec>> toOrderSpecList(){
        return orders -> {
            if (orders == null || orders.isEmpty()) return Collections.emptyList();
            List<OrderSpec> orderSpecs = new ArrayList<>();
            String[] parts = orders.split(",");
            for (int i = 0; i < parts.length; i += 2) {
                String field = parts[i].trim();
                String dir = parts[i+1].trim();
                orderSpecs.add(new OrderSpec(field, dir.equals("asc")));
            }
            return orderSpecs;
        };
    }
}
