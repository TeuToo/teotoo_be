package com.project.durumoongsil.teutoo.chat.dto.response;

import com.project.durumoongsil.teutoo.chat.domain.MsgAction;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
public class ChatReadResDto {
    private MsgAction action = MsgAction.READ;
    private Long senderId;
    private Long senderIdx;
    private Long receiverId;
    private Long receiverIdx;

    @Builder
    public ChatReadResDto(Long senderId, Long senderIdx, Long receiverId, Long receiverIdx) {
        this.senderId = senderId;
        this.senderIdx = senderIdx;
        this.receiverId = receiverId;
        this.receiverIdx = receiverIdx;
    }
}
