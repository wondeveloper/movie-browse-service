package com.vivek.imdb.exceptions;

public class MovieNotFoundException extends RuntimeException{

    private static final String MSG_FORMAT = "Invalid Exception - %s";
    public MovieNotFoundException(String message) {
        super( MSG_FORMAT.formatted(message));
    }
}
