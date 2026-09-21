package com.deboutpatriotes.api.common;

/** Requête invalide au-delà de la validation déclarative : traduite en 400. */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
