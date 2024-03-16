package com.project.durumoongsil.teutoo.estimate.dto.trainer;

import com.project.durumoongsil.teutoo.estimate.domain.TrainerEstimate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SearchEstimateProgramDto {

    private Long id;
    private String ptProgramName;

    public static SearchEstimateProgramDto fromEntity (TrainerEstimate trainerEstimate) {
        return SearchEstimateProgramDto.builder()
                .id(trainerEstimate.getPtProgram().getId())
                .ptProgramName(trainerEstimate.getPtProgram().getTitle())
                .build();
    }
}
