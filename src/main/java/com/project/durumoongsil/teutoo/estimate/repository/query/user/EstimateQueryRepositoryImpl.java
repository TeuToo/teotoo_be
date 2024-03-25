package com.project.durumoongsil.teutoo.estimate.repository.query.user;

import com.project.durumoongsil.teutoo.estimate.domain.Estimate;
import com.project.durumoongsil.teutoo.estimate.domain.TrainerEstimate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.project.durumoongsil.teutoo.estimate.domain.QEstimate.*;
import static com.project.durumoongsil.teutoo.estimate.domain.QTrainerEstimate.*;
import static com.project.durumoongsil.teutoo.member.domain.QMember.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class EstimateQueryRepositoryImpl implements EstimateQueryRepository{

    private final JPAQueryFactory factory;
    @Override
    @Transactional(readOnly = true)
    public Estimate findEstimateWithMemberName(Long estimateId) {
        return factory.selectFrom(estimate)
                .join(estimate.member, member).fetchJoin()
                .where(estimate.id.eq(estimateId))
                .fetchOne();
    }


    @Override
    public List<Estimate> pageUserEstimateWithPtAddress(Long courseId, int size, String ptAddress) {
        return factory.selectFrom(estimate)
                .join(estimate.member, member).fetchJoin()
                .where(estimate.ptAddress.like("%" + ptAddress + "%").and(estimate.id.gt(courseId)))
                .orderBy(estimate.createdAt.desc())
                .limit(size)
                .fetch();
    }

    @Override
    public List<TrainerEstimate> findTrainerEstimateNoOffset(Long cursorId, int size) {
        return factory.selectFrom(trainerEstimate)
                .join(trainerEstimate.member, member).fetchJoin()
                .where(trainerEstimate.id.gt(cursorId))
                .orderBy(trainerEstimate.createdAt.asc())
                .limit(size)
                .fetch();
    }

    @Override
    public Long getMyEstimateId(String email) {
        Estimate findEstimate = factory.selectFrom(estimate)
                .join(estimate.member, member).fetchJoin()
                .where(member.email.eq(email))
                .fetchOne();
        return findEstimate == null ? null : findEstimate.getId();
    }
}
