package com.project.durumoongsil.teutoo.exception;

public class KakaoAccessTokenException extends RuntimeException{

    public KakaoAccessTokenException() {
        super("Get access token fail...");
    }

    public KakaoAccessTokenException(String message) {
        super(message);
    }
}
