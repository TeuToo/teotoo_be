package com.project.durumoongsil.teutoo.trainer.ptprogram.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "PT 프로그램 수정 Dto")
@Getter
@Setter
public class PtProgramUpdateDto {

    @Schema(description = "프로그램 제목")
    @NotBlank(message = "프로그램 제목을 입력 해 주셔야 합니다.")
    private String title;

    @Schema(description = "프로그램 설명")
    @NotBlank(message = "프로그램 설명을 입력 해 주셔야 합니다.")
    private String content;

    @Schema(description = "프로그램 가격")
    @Min(value = 0, message = "가격을 확인해주세요")
    private int price;

    @JsonFormat(pattern = "H:mm")
    @Schema(description = "프로그램 예약 시작 시간")
    private LocalTime availableStartTime;

    @JsonFormat(pattern = "H:mm")
    @Schema(description = "프로그램 예약 종료 시간")
    private LocalTime availableEndTime;

    @Schema(description = "추가 프로그램 이미지")
    private List<MultipartFile> addPtImgList;

    @Schema(description = "삭제될 이미지")
    private List<String> delPtImgList;

}
