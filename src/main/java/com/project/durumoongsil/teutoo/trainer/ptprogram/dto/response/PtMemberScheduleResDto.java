package com.project.durumoongsil.teutoo.trainer.ptprogram.dto.response;

import com.project.durumoongsil.teutoo.common.dto.ImgResDto;
import com.project.durumoongsil.teutoo.trainer.ptprogram.constants.ReservationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Schema(description = "PT 프로그램 회원 스케쥴 DTO")
@Getter
@Builder
public class PtMemberScheduleResDto {
    @Schema(description = "트레이너 id")
    private Long trainerId;
    @Schema(description = "트레이너 이름")
    private String trainerName;
    @Schema(description = "트레이너 프로필 이미지")
    private ImgResDto imgResDto;
    @Schema(description = "프로그램 id")
    private Long programId;
    @Schema(description = "프로그램 이름")
    private String programName;
    @Schema(description = "예약 시작 시간")
    private LocalDateTime startDateTime;
    @Schema(description = "예약 종료 시간")
    private LocalDateTime endDateTime;
    @Schema(description = "예약 상태")
    private ReservationStatus status;
}
