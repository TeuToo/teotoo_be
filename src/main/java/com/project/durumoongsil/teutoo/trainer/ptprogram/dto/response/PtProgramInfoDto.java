package com.project.durumoongsil.teutoo.trainer.ptprogram.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PtProgramInfoDto {

    private String programName;
    private Long programId;

    public PtProgramInfoDto(String programName, Long programId) {
        this.programName = programName;
        this.programId = programId;
    }
}
