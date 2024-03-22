package com.project.durumoongsil.teutoo.exception;

public class ScheduleConflictException extends RuntimeException{
    public ScheduleConflictException() {
    }

    public ScheduleConflictException(String message) {
        super(message);
    }
}
