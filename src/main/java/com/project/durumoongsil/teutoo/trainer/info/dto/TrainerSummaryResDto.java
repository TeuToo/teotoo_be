package com.project.durumoongsil.teutoo.trainer.info.dto;

import com.project.durumoongsil.teutoo.common.dto.ImgResDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "트레이너 목록 반환 Dto")
@Getter
@Builder
public class TrainerSummaryResDto {

    @Schema(description = "트레이너 명", examples = "김김김")
    private String trainerName;

    @Schema(description = "헬스장 이름", examples = "GYMGYM")
    private String gymName;

    @Schema(description = "트레이너 간단소개(미리보기)", examples = "안녕하세요 김김김입니다.....")
    private String simpleIntro;

    private ImgResDto imgResDto;

    @Schema(description = "리뷰 개수")
    private int reviewCnt;

    @Schema(description = "리뷰 점수")
    private double reviewScore;
}
