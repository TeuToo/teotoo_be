package com.project.durumoongsil.teutoo.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "이미지 Dto")
@Setter
@Getter
public class ImgResDto {

    @Schema(description = "이미지 파일명", examples = "aaabbbccc.png")
    private String imgName;
    @Schema(description = "s3 이미지 주소")
    private String imgUrl;

    public ImgResDto(String imgName, String imgUrl) {
        this.imgName = imgName;
        this.imgUrl = imgUrl;
    }
}
