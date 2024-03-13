package com.project.durumoongsil.teutoo.chat.constants;

public enum ChatErrorCode {

    DEFAULT_ERROR("ERR_000", "An error occurred during processing"),
    CHAT_NOT_FOUND("ERR_001", "Requested chat not found."),
    INVALID_ACTION("ERR_002", "Invalid action request."),
    UNAUTHORIZATION_ACTION("ERR_003", "Unauthorized action request.");

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
