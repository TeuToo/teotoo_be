package com.project.durumoongsil.teutoo.trainer.ptprogram.dto;

import com.project.durumoongsil.teutoo.common.dto.ImgResDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PtProgramResDto {
    private long ptProgramId;
    private String title;
    private String content;
    private int price;
    private int ptCnt;
    private List<ImgResDto> ptImgList;
}
