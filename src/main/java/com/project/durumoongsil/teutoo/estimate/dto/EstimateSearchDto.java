package com.project.durumoongsil.teutoo.estimate.dto;

import lombok.Data;

@Data
public class EstimateSearchDto {
    private Long id;
    private String name;
    private Long price;
    private Integer ptCount;
    private String ptAddress;

}
