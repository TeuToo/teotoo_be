package com.project.durumoongsil.teutoo.chat.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;


@Schema(description = "대화 목록 미리보기 DTO")
@Getter
@Setter
public class ChatPreviewResDto {

    @Schema(description = "사용자의 id값")
    private Long memberId;

    @Schema(description = "사용자의 이름")
    private String name;

    @Schema(description = "사용자의 프로필 이미지 주소")
    private String profileImgUrl;

    @Schema(description = "최신 대화를 나눈 메시지")
    private ChatMsgResDTO latestChat;

    @Schema(description = "읽지 않은 메시지 개수")
    private Long unReadChatCnt;
}
