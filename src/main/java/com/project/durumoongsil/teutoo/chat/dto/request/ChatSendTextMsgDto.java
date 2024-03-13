package com.project.durumoongsil.teutoo.chat.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatSendTextMsgDto {
    @NotNull
    private String content;
}
