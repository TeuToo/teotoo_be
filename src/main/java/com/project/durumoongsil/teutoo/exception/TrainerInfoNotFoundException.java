package com.project.durumoongsil.teutoo.exception;

public class TrainerInfoNotFoundException extends RuntimeException{

    public TrainerInfoNotFoundException() {
        super("트레이너 소개 등록 정보를 찾을 수 없습니다.");
    }

    public TrainerInfoNotFoundException(String message) {
        super(message);
    }
}
