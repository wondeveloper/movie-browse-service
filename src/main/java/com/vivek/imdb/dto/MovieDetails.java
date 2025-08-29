package com.vivek.imdb.dto;


import java.time.LocalDateTime;

public record MovieDetails(String id,
                           String title,
                           String releaseYear,
                           LocalDateTime createdAt) {
}
