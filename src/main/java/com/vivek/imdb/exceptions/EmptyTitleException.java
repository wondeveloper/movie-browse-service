package com.vivek.imdb.exceptions;

public class EmptyTitleException extends RuntimeException{
    private static final String MSG_FORMAT = "Invalid Exception - %s";
    public EmptyTitleException(String message) {
        super( MSG_FORMAT.formatted(message));
    }
}
