package com.project.durumoongsil.teutoo.exception;

public class NotFoundUserException extends RuntimeException {

    public NotFoundUserException(String message) {
        super(message);
    }

    public NotFoundUserException(String message, Throwable cause) {
        super(message, cause);
    }
}
