package com.project.durumoongsil.teutoo.estimate.repository.query;

import com.project.durumoongsil.teutoo.estimate.domain.Estimate;
import com.project.durumoongsil.teutoo.estimate.domain.QEstimate;
import com.project.durumoongsil.teutoo.member.domain.QMember;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import static com.project.durumoongsil.teutoo.estimate.domain.QEstimate.*;
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
}
