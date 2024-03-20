package com.project.durumoongsil.teutoo.trainer.ptprogram.repository;

import com.project.durumoongsil.teutoo.trainer.ptprogram.domain.PtReservation;
import com.project.durumoongsil.teutoo.trainer.ptprogram.repository.custom.PtReservationCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PtReservationRepository extends JpaRepository<PtReservation, Long>, PtReservationCustomRepository {

    @Query("select pr from PtReservation pr inner join fetch pr.member inner join fetch pr.ptProgram where pr.id = :ptReservationId")
    Optional<PtReservation> findByIdWithMemberAndPtProgram(@Param("ptReservationId") Long ptReservationId);
}
