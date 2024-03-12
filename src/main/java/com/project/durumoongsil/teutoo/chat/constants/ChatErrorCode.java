package com.project.durumoongsil.teutoo.chat.constants;

public enum ChatErrorCode {

    DEFAULT_ERROR("ERR_000", "An error occurred during processing"),
    INVALID_ACTION("ERR_001", "Invalid action request."),
    UNAUTHORIZATION_ACTION("ERR_002", "Unauthorized action request."),
    CHAT_NOT_FOUND("ERR_003", "Requested chat not found.");

    private final String code;
    private final String message;

    ChatErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
