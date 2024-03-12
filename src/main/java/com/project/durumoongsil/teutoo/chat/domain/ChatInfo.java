package com.project.durumoongsil.teutoo.chat.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatInfo {

    private String roomId;

    private Long senderId;

    private Long senderChatIdx;

    private Long receiverId;

    private Long receiverChatIdx;

}
