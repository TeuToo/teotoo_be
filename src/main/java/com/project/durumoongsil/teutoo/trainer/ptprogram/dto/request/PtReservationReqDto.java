package com.project.durumoongsil.teutoo.trainer.ptprogram.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Schema(description = "PT 프로그램 예약 Dto")
@Getter
@Setter
public class PtReservationReqDto {

    @Schema(description = "PT 프로그램 예약할 시작 시간", examples = "2024-03-02T11:00")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startTime;

    @Schema(description = "PT 프로그램 예약할 종료 시간", examples = "2024-03-02T12:30")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endTime;

    @Schema(description = "PT 프로그램 예약할 프로그램 id")
    private Long programId;
}
