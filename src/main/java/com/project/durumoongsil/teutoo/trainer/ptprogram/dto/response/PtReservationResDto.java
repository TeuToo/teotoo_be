package com.project.durumoongsil.teutoo.trainer.ptprogram.dto.response;

import lombok.Getter;

@Getter
public class PtReservationResDto {
    private Long reservationId;

    public PtReservationResDto(Long reservationId) {
        this.reservationId = reservationId;
    }
}
