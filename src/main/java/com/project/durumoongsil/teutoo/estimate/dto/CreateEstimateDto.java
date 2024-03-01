package com.project.durumoongsil.teutoo.estimate.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class CreateEstimateDto {

    @NotNull
    @Min(0)
    private Long price;
    @NotNull
    @Min(0)
    private Integer ptCount;
    @NotEmpty
    private String ptAddress;
}
