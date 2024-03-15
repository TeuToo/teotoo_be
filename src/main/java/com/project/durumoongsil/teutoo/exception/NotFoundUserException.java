package com.project.durumoongsil.teutoo.exception;

public class NotFoundUserException extends RuntimeException {

    public NotFoundUserException() {
        super("사용자를 찾을 수 없습니다.");
    }

    public NotFoundUserException(String message) {
        super(message);
    }

    public NotFoundUserException(String message, Throwable cause) {
        super(message, cause);
    }
}
