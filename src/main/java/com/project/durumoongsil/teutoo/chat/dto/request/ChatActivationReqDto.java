package com.project.durumoongsil.teutoo.chat.dto.request;

import com.project.durumoongsil.teutoo.chat.constants.ChatActivationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Schema(description = "채팅방 조회 DTO")
public class ChatActivationReqDto {

    @Schema(description = "채팅 활성화 API 호출 타입, INFO: 채팅방만 조회, MEMBER_RESERVE: 회원이 예약요청, TRAINER_RESERVE: 트레이너가 예약요청")
    private ChatActivationType activationType;
    private MemberReservationDto memberReservationDto;
    private TrainerReservationDto trainerReservationDto;
}
