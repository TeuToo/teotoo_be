package com.project.durumoongsil.teutoo.trainer.ptprogram.repository.custom;

import java.time.LocalDateTime;

public interface PtReservationCustomRepository {

    Long countPtReservationByProgramIdANdDateTimeRange(Long programId, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
