package com.project.durumoongsil.teutoo.estimate.dto.trainer;

import com.project.durumoongsil.teutoo.trainer.ptprogram.domain.PtProgram;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateTrainerEstimateDto {

    @NotNull
    @Min(0)
    @Schema(description = "PT 가격")
    private int price;

    @NotEmpty
    @Schema(description = "PT 주소")
    private String ptAddress;

    @Schema(description = "PT 프로그램 ID")
    private Long programId;
}
