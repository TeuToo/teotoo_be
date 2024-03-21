package com.project.durumoongsil.teutoo.trainer.ptprogram.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "PT 프로그램 예약 Dto")
@Getter
public class PtReservationResDto {

    @Schema(description = "PT 프로그램 예약을 한 이후 예약된 id")
    private Long reservationId;

    public PtReservationResDto(Long reservationId) {
        this.reservationId = reservationId;
    }
}
