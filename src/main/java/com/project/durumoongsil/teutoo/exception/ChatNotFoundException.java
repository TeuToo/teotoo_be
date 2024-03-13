package com.project.durumoongsil.teutoo.exception;

public class ChatNotFoundException extends RuntimeException{

    public ChatNotFoundException() {
    }

    public ChatNotFoundException(String message) {
        super(message);
    }
}
