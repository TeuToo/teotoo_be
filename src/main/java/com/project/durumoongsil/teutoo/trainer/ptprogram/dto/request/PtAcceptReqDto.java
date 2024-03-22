package com.project.durumoongsil.teutoo.trainer.ptprogram.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "PT 프로그램 예약 확인 Dto")
@Getter
@Setter
public class PtAcceptReqDto {

    @Schema(description = "예약 확인을 할 해당 예약 id")
    private Long reservationId;
}
