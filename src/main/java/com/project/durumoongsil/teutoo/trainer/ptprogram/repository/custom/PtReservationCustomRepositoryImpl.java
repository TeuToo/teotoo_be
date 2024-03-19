package com.project.durumoongsil.teutoo.trainer.ptprogram.repository.custom;

import com.project.durumoongsil.teutoo.trainer.ptprogram.domain.QPtProgram;
import com.project.durumoongsil.teutoo.trainer.ptprogram.domain.QPtReservation;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PtReservationCustomRepositoryImpl implements PtReservationCustomRepository{

    private final JPAQueryFactory queryFactory;

    QPtProgram qPtProgram = QPtProgram.ptProgram;
    QPtReservation qPtReservation = QPtReservation.ptReservation;

    /**
     * 해당 program 의 예약 스케쥴의 startDateTime <= , <= endDateTime 조건에 속하는 레코드의 개수 파악
     * @param programId
     * @param startDateTime
     * @param endDateTime
     * @return 예약 중인 레코드 개수
     */
    @Override
    public Long countPtReservationByProgramIdANdDateTimeRange(Long programId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return queryFactory.select(qPtReservation.count())
                .from(qPtProgram)
                .innerJoin(qPtProgram.ptReservationList, qPtReservation)
                .where(qPtProgram.id.eq(programId).and(
                        qPtReservation.startDateTime.loe(startDateTime)).and(
                        qPtReservation.endDateTime.goe(endDateTime)
                ))
                .fetchOne();
    }
}
