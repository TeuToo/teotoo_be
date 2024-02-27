package com.project.durumoongsil.teutoo.trainer.dto;

import com.project.durumoongsil.teutoo.trainer.dto.ImgResDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Schema(description = "트레이너 소개 페이지 응답 Dto")
@Getter
@Setter
@Builder
public class TrainerInfoResDto {

    @Schema(description = "트레이너 이름", examples = "김헬창")
    private String trainerName;

    @Schema(description = "s3 버킷 이미지 주소")
    private String trainerImgUrl;

    @Schema(description = "트레이너의 지역", examples = "서울시 강서구 화곡동")
    private String trainerAddress;

    @Schema(description = "헬스장 이름", examples = "xxx gym")
    private String gymName;

    @Schema(description = "트레이너 간단 소개글", examples = "안녕하세요 트레이너 소개입니다...")
    private String simpleIntro;

    @Schema(description = "트레이너 소개글", examples = "매번 마음에 들지 않아도 상담 후")
    private String introContent;

    @Schema(description = "자격사항 이미지")
    private List<ImgResDto> careerImgList;

    // 후기는 나중에...
    private int reviewCnt;

    private double reviewScore;

    // 프로그램 리스트도...
}
