package com.project.durumoongsil.teutoo.chat.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReservationReqDto {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long programId;
}
