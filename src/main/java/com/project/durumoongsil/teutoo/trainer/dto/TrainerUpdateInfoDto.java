package com.project.durumoongsil.teutoo.trainer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Schema(description = "트레이너 소개 등록/수정 Dto")
@Getter
@Setter
@Builder
public class TrainerUpdateInfoDto {

    @Schema(description = "헬스장 이름", examples = "xxx헬스장")
    @NotBlank(message = "헬스장 이름을 입력 해 주셔야 합니다.")
    private String gymName;

    @Schema(description = "트레이너 간단 소개글", examples = "안녕하세요 트레이너 소개입니다...")
    @NotBlank(message = "트레이너 간단 소개글은 반드시 등록 해 주셔야 합니다.")
    private String simpleIntro;

    @Schema(description = "트레이너 소개글", examples = "매번 마음에 들지 않아도 상담 후")
    @NotBlank(message = "트레이너 소개글은 반드시 등록 해 주셔야 합니다.")
    private String introContent;

    @Schema(description = "경력사항 이미지")
    private List<MultipartFile> careerImgList;

    @Schema(description = "삭제될 이미지(파일명 확장자 포함)")
    private List<String> deletedImgList;
}
