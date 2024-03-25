package com.project.durumoongsil.teutoo.estimate.dto.user;

import com.project.durumoongsil.teutoo.estimate.domain.Estimate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PageUserEstimateDto {
    private Long memberId;
    private Long estimateId;
    private Integer price;
    private String name;
    private String profileImagePath;
}
