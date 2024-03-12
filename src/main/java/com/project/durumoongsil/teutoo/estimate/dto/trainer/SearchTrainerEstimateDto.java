package com.project.durumoongsil.teutoo.estimate.dto.trainer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SearchTrainerEstimateDto {

    private Long id;

    @NotNull
    @Min(0)
    @Schema(description = "PT 가격")
    private int price;

    @NotEmpty
    @Schema(description = "PT 주소")
    private String ptAddress;

    @Schema(description = "PT 프로그램")
    private SearchEstimateProgramDto ptProgram;

    private String name;
}
