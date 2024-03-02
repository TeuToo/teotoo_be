package com.project.durumoongsil.teutoo.trainer.ptprogram.dto;

import com.project.durumoongsil.teutoo.common.dto.ImgResDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PtProgramManageResDto {

    private String trainerName;
    private ImgResDto trainerImg;
    private List<PtProgramResDto> ptProgramResList;
}
