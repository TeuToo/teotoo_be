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
    private Long memberId;
    private String memberName;
    private Long trainerId;
    private String trainerName;
    private String programName;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private ReservationStatus status;
}
