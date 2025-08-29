package com.vivek.imdb.util;

import com.vivek.imdb.dto.MovieDetails;
import com.vivek.imdb.dto.MovieDto;
import com.vivek.imdb.entity.Movie;

import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;

public class EntityMapper {

    public static Movie convertToMovie(MovieDto movieDto){
        return Movie.create(UUID.randomUUID().toString(), movieDto.getTitle(), movieDto.getReleaseYear(), Instant.EPOCH, true);
    }

    public static MovieDetails convertToMovieDetails(Movie movieDto){
        return new MovieDetails(movieDto.getId(), movieDto.getTitle(), movieDto.getReleaseYear(), movieDto.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDateTime());
    }

}
