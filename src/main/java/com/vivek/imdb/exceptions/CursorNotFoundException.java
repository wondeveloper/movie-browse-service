package com.vivek.imdb.exceptions;

public class CursorNotFoundException extends RuntimeException{
    private static final String MSG_FORMAT = "Invalid Exception - %s";
    public CursorNotFoundException(String message) {
        super( MSG_FORMAT.formatted(message));
    }
}
