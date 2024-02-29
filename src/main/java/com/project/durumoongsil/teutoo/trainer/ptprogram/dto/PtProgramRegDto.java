package com.project.durumoongsil.teutoo.trainer.ptprogram.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Schema(description = "PT 프로그램 등록 Dto")
@Getter
@Setter
public class PtProgramRegDto {

    @Schema(description = "프로그램 제목")
    private String title;
    @Schema(description = "프로그램 설명")
    private String content;
    @Schema(description = "프로그램 가격")
    private int price;
    @Schema(description = "프로그램 횟수")
    private int ptCnt;
    @Schema(description = "추가 프로그램 이미지")
    private List<MultipartFile> programImgList;
}
