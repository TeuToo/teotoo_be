package com.project.durumoongsil.teutoo.chat.dto.response;

import com.project.durumoongsil.teutoo.chat.constants.MsgAction;
import com.project.durumoongsil.teutoo.chat.constants.MsgType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Schema(description = "메시지 DTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMsgResDTO {

    @Schema(description = "메시지의 index 값")
    private Long msgIdx;

    @Schema(description = "메시지 형태", examples = {"SEND", "READ"})
    private MsgAction msgAction;

    @Schema(description = "콘텐츠 타입", examples = {"TEXT", "IMG", "RESERVE"})
    private MsgType contentType;

    @Schema(description = "메시지 본문")
    private String content;

    @Schema(description = "메시지 보낸 시각")
    private LocalDateTime createdAt;

    @Schema(description = "메시지를 보낸 사용자 id")
    private Long senderId;

    @Builder
    public ChatMsgResDTO(MsgAction msgAction, MsgType contentType, String content, LocalDateTime createdAt, Long senderId, Long msgIdx) {
        this.msgAction = msgAction;
        this.contentType = contentType;
        this.content = content;
        this.createdAt = createdAt;
        this.senderId = senderId;
        this.msgIdx = msgIdx;
    }
}
