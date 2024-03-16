package com.project.durumoongsil.teutoo.estimate.dto.trainer;

import com.project.durumoongsil.teutoo.estimate.domain.TrainerEstimate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SearchTrainerEstimateDto {

    private Long estimateId;

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

    public static SearchTrainerEstimateDto fromEntity(TrainerEstimate trainerEstimate, SearchEstimateProgramDto searchEstimateProgramDto) {
        return SearchTrainerEstimateDto.builder()
                .estimateId(trainerEstimate.getId())
                .price(trainerEstimate.getPrice())
                .ptAddress(trainerEstimate.getPtCenterAddress())
                .ptProgram(searchEstimateProgramDto)
                .name(trainerEstimate.getMember().getName())
                .build();
    }
}
