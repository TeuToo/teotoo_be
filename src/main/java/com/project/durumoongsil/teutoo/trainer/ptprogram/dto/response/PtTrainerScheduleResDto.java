package com.project.durumoongsil.teutoo.trainer.ptprogram.dto.response;

import com.project.durumoongsil.teutoo.common.dto.ImgResDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Schema(description = "PT 프로그램 트레이너 스케쥴 DTO")
@Getter
@Builder
public class PtTrainerScheduleResDto {
    @Schema(description = "회원 id")
    private Long memberId;
    @Schema(description = "회원 이름")
    private String memberName;
    @Schema(description = "프로필 이미지")
    private ImgResDto imgResDto;
    @Schema(description = "예약 시작 시간")
    private LocalDateTime startDateTime;
    @Schema(description = "예약 종료 시간")
    private LocalDateTime endDateTime;
}
