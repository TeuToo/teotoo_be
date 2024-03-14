package com.project.durumoongsil.teutoo.trainer.ptprogram.dto.response;

import com.project.durumoongsil.teutoo.common.dto.ImgResDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Schema(description = "PT 프로그램 관리 Dto")
@Getter
@Builder
public class PtProgramManageResDto {

    @Schema(description = "트레이너 이름")
    private String trainerName;
    @Schema(description = "트레이너 프로필 이미지")
    private ImgResDto trainerImg;
    @Schema(description = "PT 프로그램 리스트")
    private List<PtProgramResDto> ptProgramResList;
}
