package com.project.durumoongsil.teutoo.exception;

public class UserUnauthorizedException extends RuntimeException {
    public UserUnauthorizedException() {
    }

    public UserUnauthorizedException(String message) {
        super(message);
    }
}
