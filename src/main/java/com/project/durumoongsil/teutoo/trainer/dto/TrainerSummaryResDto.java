package com.project.durumoongsil.teutoo.trainer.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class TrainerSummaryResDto {

    private String trainerName;
    private String gymName;
    private String simpleIntro;
    private ImgResDto imgResDto;
    private int reviewCnt;
    private double reviewScore;
}
