package com.project.durumoongsil.teutoo.estimate.repository.query.user;

import com.project.durumoongsil.teutoo.estimate.domain.Estimate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.project.durumoongsil.teutoo.estimate.domain.QEstimate.*;
import static com.project.durumoongsil.teutoo.estimate.domain.QTrainerEstimate.trainerEstimate;
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
    public Page<Estimate> findAllWithPtAddressPage(Pageable pageable, String ptAddress) {
        log.info("ptAddress = {}", ptAddress);
        List<Estimate> estimates = factory.selectFrom(estimate)
                .join(estimate.member, member).fetchJoin()
                .where(estimate.ptAddress.like("%" + ptAddress + "%"))
                .orderBy(estimate.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();


        Long totalCount = factory.select(estimate.count())
                .from(estimate)
                .fetchOne();

        return new PageImpl<>(estimates, pageable, totalCount);
    }

    @Override
    public List<Estimate> findEstimateAfterCursor(Long cursorId, int size) {
        return  factory.selectFrom(estimate)
                .join(estimate.member, member).fetchJoin()
                .where(estimate.id.gt(cursorId))
                .orderBy(estimate.id.asc())
                .limit(size)
                .fetch();
    }
}
