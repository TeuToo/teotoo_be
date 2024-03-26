package com.project.durumoongsil.teutoo.trainer.ptprogram.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Schema(description = "PT 프로그램 예약 시간 DTO")
@Getter
@Setter
public class PtProgramReservedTimeDto {
    @Schema(description = "예약 시작 시간")
    private LocalDateTime startDateTime;
    @Schema(description = "예약 종료 시간")
    private LocalDateTime endDateTime;

    public PtProgramReservedTimeDto(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }
}
