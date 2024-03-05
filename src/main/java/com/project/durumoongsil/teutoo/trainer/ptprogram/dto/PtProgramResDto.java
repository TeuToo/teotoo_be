package com.project.durumoongsil.teutoo.trainer.ptprogram.dto;

import com.project.durumoongsil.teutoo.common.dto.ImgResDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Schema(description = "PT 프로그램 정보 DTO")
@Getter
@Setter
@Builder
public class PtProgramResDto {

    @Schema(description = "프로그램 ID")
    private long ptProgramId;

    @Schema(description = "프로그램 제목")
    private String title;

    @Schema(description = "프로그램 내용")
    private String content;

    @Schema(description = "프로그램 가격")
    private int price;

    @Schema(description = "프로그램 횟수")
    private int ptCnt;

    @Schema(description = "PT 프로그램 이미지 리스트")
    private List<ImgResDto> ptProgramImgList;
}
