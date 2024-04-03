package com.project.durumoongsil.teutoo.chat.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Schema(description = "대화방 관련 데이터 DTO")
@Builder
@Getter
public class ChatActivationResDto {
    @Schema(description = "대화방 ID")
    private String roomId;
    @Schema(description = "자신의 메시지 index")
    private Long senderIdx;
    @Schema(description = "상대방의 메시지 index")
    private Long receiverIdx;
    @Schema(description = "메시지 목록")
    private List<ChatMsgResDTO> messages;

}
