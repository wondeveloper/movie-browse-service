package com.vivek.imdb.exceptions;

public class InvalidYearException extends RuntimeException{

    private static final String MSG_FORMAT = "Invalid Exception - %s";
    public InvalidYearException(String message) {
        super( MSG_FORMAT.formatted(message));
    }
}
