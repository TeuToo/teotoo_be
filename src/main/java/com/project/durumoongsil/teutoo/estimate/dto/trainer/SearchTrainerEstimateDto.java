package com.project.durumoongsil.teutoo.estimate.dto.trainer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SearchTrainerEstimateDto {

    @NotNull
    @Min(0)
    @Schema(description = "PT 가격")
    private Long price;

    @NotEmpty
    @Schema(description = "PT 주소")
    private String ptAddress;

    @Schema(description = "PT 프로그램")
    private SearchEstimateProgramDto ptProgram;
}
