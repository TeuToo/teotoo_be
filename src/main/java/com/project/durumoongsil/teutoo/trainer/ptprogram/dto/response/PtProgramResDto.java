package com.project.durumoongsil.teutoo.trainer.ptprogram.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.durumoongsil.teutoo.common.dto.ImgResDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;

@Schema(description = "PT 프로그램 정보 DTO")
@Getter
@Setter
@Builder
public class PtProgramResDto {

    @Schema(description = "트레이너 ID")
    private long trainerId;

    @Schema(description = "프로그램 ID")
    private long ptProgramId;

    @Schema(description = "프로그램 제목")
    private String title;

    @Schema(description = "프로그램 내용")
    private String content;

    @Schema(description = "프로그램 가격")
    private int price;

    @JsonFormat(pattern = "H:mm")
    @Schema(description = "프로그램 예약 시작 시간")
    private LocalTime availableStartTime;

    @JsonFormat(pattern = "H:mm")
    @Schema(description = "프로그램 예약 종료 시간")
    private LocalTime availableEndTime;

    @Schema(description = "PT 프로그램 이미지 리스트")
    private List<ImgResDto> ptProgramImgList;
}
