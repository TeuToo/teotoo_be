package com.project.durumoongsil.teutoo.common.domain;

public enum FilePath {

    MEMBER_PROFILE("member_profile"),
    TRAINER_INFO("trainer_info"),
    PT_PROGRAM("pt_program");

    private final String path;

    FilePath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}

