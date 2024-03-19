package com.project.durumoongsil.teutoo.trainer.ptprogram.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PtProgramReservedTimeDto {
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    public PtProgramReservedTimeDto(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }
}
