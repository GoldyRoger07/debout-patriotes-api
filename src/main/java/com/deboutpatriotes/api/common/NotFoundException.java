package com.deboutpatriotes.api.common;

/** Ressource demandée absente : traduite en 404 par {@link ApiExceptionHandler}. */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
