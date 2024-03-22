package com.project.durumoongsil.teutoo.trainer.ptprogram.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "PT 프로그램 예약 확인 Dto")
@Getter
@Setter
public class PtAcceptResDto {

    @Schema(description = "PT 프로그램 완료가 된 이후 해당 예약 id")
    private Long reservationId;

    public PtAcceptResDto(Long reservationId) {
        this.reservationId = reservationId;
    }
}
