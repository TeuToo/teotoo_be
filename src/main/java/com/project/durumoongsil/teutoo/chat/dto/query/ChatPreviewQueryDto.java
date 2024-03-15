package com.project.durumoongsil.teutoo.chat.dto.query;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChatPreviewQueryDto {

    private Long aMemberId;
    private Long bMemberId;
    private String aMemberName;
    private String bMemberName;
    private String aMemberImgName;
    private String bMemberImgName;
    private Long aMemberChatIdx;
    private Long bMemberChatIdx;
    private ChatMsgQueryDto chatMsgQueryDTO;

    @QueryProjection
    public ChatPreviewQueryDto(Long aMemberId, Long bMemberId, String aMemberName, String bMemberName, String aMemberImgName,
                               String bMemberImgName, Long aMemberChatIdx, Long bMemberChatIdx, ChatMsgQueryDto chatMsgQueryDTO) {
        this.aMemberId = aMemberId;
        this.bMemberId = bMemberId;
        this.aMemberName = aMemberName;
        this.bMemberName = bMemberName;
        this.aMemberImgName = aMemberImgName;
        this.bMemberImgName = bMemberImgName;
        this.aMemberChatIdx = aMemberChatIdx;
        this.bMemberChatIdx = bMemberChatIdx;
        this.chatMsgQueryDTO = chatMsgQueryDTO;
    }
}
