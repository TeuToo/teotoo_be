package com.project.durumoongsil.teutoo.trainer.ptprogram.repository.custom;

import com.project.durumoongsil.teutoo.member.domain.QMember;
import com.project.durumoongsil.teutoo.trainer.info.domain.QTrainerInfo;
import com.project.durumoongsil.teutoo.trainer.ptprogram.constants.ReservationStatus;
import com.project.durumoongsil.teutoo.trainer.ptprogram.domain.PtReservation;
import com.project.durumoongsil.teutoo.trainer.ptprogram.domain.QPtProgram;
import com.project.durumoongsil.teutoo.trainer.ptprogram.domain.QPtReservation;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PtReservationCustomRepositoryImpl implements PtReservationCustomRepository{

    private final JPAQueryFactory queryFactory;

    QPtProgram qPtProgram = QPtProgram.ptProgram;
    QPtReservation qPtReservation = QPtReservation.ptReservation;
    QTrainerInfo qTrainerInfo = QTrainerInfo.trainerInfo;
    QMember qMember = QMember.member;
    QMember qTrainer = new QMember("trainer");

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
                .from(qPtReservation)
                .innerJoin(qPtReservation.ptProgram, qPtProgram)
                .innerJoin(qPtProgram.trainerInfo, qTrainerInfo)
                .where(
                        qPtProgram.id.eq(programId)
                                .and(this.isWithinTimeRange(startDateTime, endDateTime))
                                .and(this.isReservedOrPending())
                )
                .fetchOne();
    }

    private BooleanBuilder isWithinTimeRange(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        booleanBuilder.and(qPtReservation.startDateTime.lt(endDateTime));
        booleanBuilder.and(qPtReservation.endDateTime.gt(startDateTime));

        return booleanBuilder;
    }

    private BooleanBuilder isReservedOrPending() {
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        booleanBuilder.or(qPtReservation.status.eq(ReservationStatus.PENDING));
        booleanBuilder.or(qPtReservation.status.eq(ReservationStatus.RESERVED));

        return booleanBuilder;
    }

    public List<PtReservation> findByMemberIdWithPtProgramAndTrainerInfoAndMember(Long memberId) {
        return queryFactory
                .selectFrom(qPtReservation)
                .innerJoin(qPtReservation.member, qMember)
                .innerJoin(qPtReservation.ptProgram, qPtProgram).fetchJoin()
                .innerJoin(qPtProgram.trainerInfo, qTrainerInfo).fetchJoin()
                .innerJoin(qTrainerInfo.member, qTrainer).fetchJoin()
                .where(
                        qMember.id.eq(memberId).and(
                        qPtReservation.endDateTime.goe(LocalDateTime.now())).and(
                        qPtReservation.status.ne(ReservationStatus.CANCELED
                        ))
                )
                .fetch();
    }
}
