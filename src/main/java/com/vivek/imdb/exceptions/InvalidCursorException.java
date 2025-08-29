package com.vivek.imdb.exceptions;

public class InvalidCursorException extends RuntimeException{
    private static final String MSG_FORMAT = "Invalid Exception - %s";
    public InvalidCursorException(String message) {
        super( MSG_FORMAT.formatted(message));
    }
}
