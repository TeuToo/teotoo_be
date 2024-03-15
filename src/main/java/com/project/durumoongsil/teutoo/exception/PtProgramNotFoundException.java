package com.project.durumoongsil.teutoo.exception;

public class PtProgramNotFoundException extends RuntimeException{
    public PtProgramNotFoundException() {
        super("트레이너 프로그램 등록 정보를 찾을 수 없습니다.");
    }

    public PtProgramNotFoundException(String message) {
        super(message);
    }
}
