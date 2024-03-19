package com.project.durumoongsil.teutoo.estimate.repository.query.trainer;

import com.project.durumoongsil.teutoo.estimate.domain.TrainerEstimate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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

    @Override
    public Page<TrainerEstimate> pageTrainerEstimateWithPtAddress(Pageable pageable) {
        List<TrainerEstimate> estimates = factory.selectFrom(trainerEstimate)
                .join(trainerEstimate.member, member).fetchJoin()
                .orderBy(trainerEstimate.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();


        Long totalCount = factory.select(trainerEstimate.count())
                .from(trainerEstimate)
                .fetchOne();

        return new PageImpl<>(estimates, pageable, totalCount);
    }

    @Override
    public Optional<TrainerEstimate> findByEstimateIdWithMember(Long estimateId) {
        return Optional.ofNullable(factory.selectFrom(trainerEstimate)
                .join(trainerEstimate.member, member).fetchJoin()
                .where(trainerEstimate.id.eq(estimateId))
                .fetchOne());
    }
}
