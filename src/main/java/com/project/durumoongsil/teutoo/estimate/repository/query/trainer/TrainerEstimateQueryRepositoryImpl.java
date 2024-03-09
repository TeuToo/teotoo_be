package com.project.durumoongsil.teutoo.estimate.repository.query.trainer;

import com.project.durumoongsil.teutoo.estimate.domain.TrainerEstimate;
import com.project.durumoongsil.teutoo.trainer.ptprogram.domain.PtProgram;
import com.project.durumoongsil.teutoo.trainer.ptprogram.domain.QPtProgram;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import static com.project.durumoongsil.teutoo.member.domain.QMember.*;
import static com.project.durumoongsil.teutoo.estimate.domain.QTrainerEstimate.*;
import static com.project.durumoongsil.teutoo.trainer.ptprogram.domain.QPtProgram.ptProgram;

@RequiredArgsConstructor
public class TrainerEstimateQueryRepositoryImpl implements TrainerEstimateQueryRepository{

    private final JPAQueryFactory factory;

    @Override
    @Transactional(readOnly = true)
    public TrainerEstimate findByPtProgramIdWithFetch(Long trainerEstimateId) {
        return factory.selectFrom(trainerEstimate)
                .join(trainerEstimate.member, member).fetchJoin()
                .join(trainerEstimate.ptProgram, ptProgram).fetchJoin()
                .where(trainerEstimate.id.eq(trainerEstimateId))
                .fetchOne();
    }
}
