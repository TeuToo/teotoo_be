package com.project.durumoongsil.teutoo.exception;

public class UnauthorizedActionException extends RuntimeException {
    public UnauthorizedActionException() {
        super();
    }

    public UnauthorizedActionException(String message) {
        super(message);
    }
}
