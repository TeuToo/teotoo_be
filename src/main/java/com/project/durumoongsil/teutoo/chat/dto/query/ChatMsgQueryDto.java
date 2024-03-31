package com.project.durumoongsil.teutoo.chat.dto.query;

import com.project.durumoongsil.teutoo.chat.constants.MsgType;
import com.project.durumoongsil.teutoo.trainer.ptprogram.constants.ReservationStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ChatMsgQueryDto {

    private Long msgIdx;

    private Long senderId;

    private String senderName;

    private MsgType contentType;

    private LocalDateTime createdAt;

    private String textContent;

    private String imgPath;

    private String imgName;

    private Long programId;

    private String programName;

    private Integer ptProgramPrice;

    private String gymAddress;

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    private ReservationStatus status;

    @QueryProjection
    public ChatMsgQueryDto(Long msgIdx, Long senderId, String senderName, MsgType contentType, LocalDateTime createdAt, String textContent,
                           String imgPath, String imgName, Long programId, String programName, Integer ptProgramPrice, String gymAddress,
                           LocalDateTime startDateTime, LocalDateTime endDateTime, ReservationStatus status) {
        this.msgIdx = msgIdx;
        this.senderId = senderId;
        this.senderName = senderName;
        this.contentType = contentType;
        this.createdAt = createdAt;
        this.textContent = textContent;
        this.imgPath = imgPath;
        this.imgName = imgName;
        this.programId = programId;
        this.programName = programName;
        this.ptProgramPrice = ptProgramPrice;
        this.gymAddress = gymAddress;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.status = status;
    }
}
