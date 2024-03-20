package com.project.durumoongsil.teutoo.chat.dto.response;

import com.project.durumoongsil.teutoo.trainer.ptprogram.constants.ReservationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PtReservationMsgDto {
    private Long reservationId;
    private Long programId;
    private String senderName;
    private String programName;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private ReservationStatus status;
}
