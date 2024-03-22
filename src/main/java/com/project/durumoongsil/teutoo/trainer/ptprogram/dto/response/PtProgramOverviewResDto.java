package com.project.durumoongsil.teutoo.trainer.ptprogram.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class PtProgramOverviewResDto {
    private List<PtProgramInfoDto> ptProgramInfo;
    private List<PtProgramReservedTimeDto> reservedDateTime;

    public PtProgramOverviewResDto(List<PtProgramInfoDto> ptProgramInfo, List<PtProgramReservedTimeDto> reservedDateTime) {
        this.ptProgramInfo = ptProgramInfo;
        this.reservedDateTime = reservedDateTime;
    }
}
