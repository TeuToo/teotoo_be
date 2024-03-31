package com.project.durumoongsil.teutoo.trainer.ptprogram.repository.custom;

import com.project.durumoongsil.teutoo.trainer.ptprogram.domain.PtReservation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PtReservationCustomRepository {

    Long countPtReservationByProgramIdANdDateTimeRange(Long trainerId, LocalDateTime startDateTime, LocalDateTime endDateTime);
    List<PtReservation> findByMemberIdWithPtProgramAndTrainerInfoAndMember(Long memberId);
    Optional<Long> findTrainerIdById(Long reservationId);
}
