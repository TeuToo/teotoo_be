package com.project.durumoongsil.teutoo.chat.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "트레이너가 예약요청하기 위한 DTO")
@Getter
@Setter
public class TrainerReservationDto {

    @Schema(description = "가격")
    private int price;
    @Schema(description = "주소")
    private String address;
}
