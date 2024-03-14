package com.project.durumoongsil.teutoo.trainer.ptprogram.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;
import java.util.List;

@Schema(description = "PT 프로그램 등록 Dto")
@Getter
@Setter
public class PtProgramRegDto {

    @Schema(description = "프로그램 제목")
    @NotBlank(message = "프로그램 제목을 입력 해 주셔야 합니다.")
    private String title;

    @Schema(description = "프로그램 설명")
    @NotBlank(message = "프로그램 설명을 입력 해 주셔야 합니다.")
    private String content;

    @Schema(description = "프로그램 가격")
    @Min(value = 0, message = "가격을 확인해주세요")
    private int price;

    @DateTimeFormat(pattern = "H:mm")
    @Schema(description = "프로그램 예약 시작 시간", type = "string", examples = "9:00")
    private LocalTime availableStartTime;

    @DateTimeFormat(pattern = "H:mm")
    @Schema(description = "프로그램 예약 종료 시간", type = "string", examples = "10:30")
    private LocalTime availableEndTime;

    @Schema(description = "추가 프로그램 이미지")
    private List<MultipartFile> addPtImgList;
}
