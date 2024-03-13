package com.project.durumoongsil.teutoo.chat.dto.query;

import com.project.durumoongsil.teutoo.chat.constants.MsgType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ChatMsgQueryDTO {
    private Long msgIdx;

    private Long senderId;

    private MsgType contentType;

    private LocalDateTime createdAt;

    private String textContent;

    private String imgPath;

    private String imgName;

    private String programName;

    private String programSchedule;

    private Boolean programConfirm;

    @QueryProjection
    public ChatMsgQueryDTO(Long msgIdx, Long senderId, MsgType contentType, LocalDateTime createdAt, String textContent,
                           String imgPath, String imgName, String programName, String programSchedule, Boolean programConfirm) {
        this.msgIdx = msgIdx;
        this.senderId = senderId;
        this.contentType = contentType;
        this.createdAt = createdAt;
        this.textContent = textContent;
        this.imgPath = imgPath;
        this.imgName = imgName;
        this.programName = programName;
        this.programSchedule = programSchedule;
        this.programConfirm = programConfirm;
    }
}
