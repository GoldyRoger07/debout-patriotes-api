package com.deboutpatriotes.api.common;

/** Violation d'une contrainte métier (ex. slug déjà utilisé) : traduite en 409. */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
