package com.project.durumoongsil.teutoo.common.domain;

public enum FilePath {

    MEMBER_PROFILE("member_profile"),
    TRAINER_INFO("trainer_info"),
    PT_PROGRAM("pt_program"),

    CHAT_IMG("chat_img");

    private final String path;

    FilePath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}

