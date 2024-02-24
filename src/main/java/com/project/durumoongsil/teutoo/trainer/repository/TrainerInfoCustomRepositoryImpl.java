package com.project.durumoongsil.teutoo.trainer.repository;

import com.project.durumoongsil.teutoo.member.domain.Member;
import com.project.durumoongsil.teutoo.member.domain.QMember;
import com.project.durumoongsil.teutoo.trainer.domain.QTrainerInfo;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class TrainerInfoCustomRepositoryImpl implements TrainerInfoCustomRepository{

    @PersistenceContext
    EntityManager em;

    @Override
    @Transactional
    public Optional<Member> findMemberByIdWithTrainerInfo(Long memberId) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        QMember qMember = QMember.member;
        QTrainerInfo qTrainerInfo = QTrainerInfo.trainerInfo;

        Member member = queryFactory.selectFrom(qMember)
                .where(qMember.id.eq(memberId))
                .leftJoin(qMember.trainerInfo, qTrainerInfo).fetchJoin()
                .fetchFirst();

        if (member == null)
            return Optional.empty();

        return Optional.of(member);
    }

}
