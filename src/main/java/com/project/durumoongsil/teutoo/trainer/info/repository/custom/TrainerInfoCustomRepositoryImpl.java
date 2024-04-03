package com.project.durumoongsil.teutoo.trainer.info.repository.custom;

import com.project.durumoongsil.teutoo.member.domain.Member;
import com.project.durumoongsil.teutoo.member.domain.QMember;
import com.project.durumoongsil.teutoo.member.domain.Role;
import com.project.durumoongsil.teutoo.trainer.info.domain.QTrainerInfo;
import com.project.durumoongsil.teutoo.trainer.info.domain.TrainerInfo;
import com.project.durumoongsil.teutoo.trainer.ptprogram.domain.QPtProgram;
import com.project.durumoongsil.teutoo.trainer.ptprogram.domain.QPtReservation;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

@Component
@RequiredArgsConstructor
public class TrainerInfoCustomRepositoryImpl implements TrainerInfoCustomRepository{

    private final JPAQueryFactory queryFactory;

    QMember qMember = QMember.member;
    QTrainerInfo qTrainerInfo = QTrainerInfo.trainerInfo;
    QPtProgram qPtProgram = QPtProgram.ptProgram;
    QPtReservation qPtReservation = QPtReservation.ptReservation;

    @Override
    public Optional<Member> findMemberByIdWithTrainerInfo(String userEmail) {

        Member member = queryFactory.selectFrom(qMember)
                .where(
                        qMember.email.eq(userEmail),
                        qMember.role.eq(Role.TRAINER)
                )
                .leftJoin(qMember.trainerInfo, qTrainerInfo).fetchJoin()
                .fetchFirst();

        if (member == null)
            return Optional.empty();

        return Optional.of(member);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TrainerInfo> findBySearchCondition(PageRequest pageRequest, String search) {


        BooleanBuilder searchCondition = new BooleanBuilder();
        searchCondition.or(this.gymContains(search));
        searchCondition.or(this.trainerNameContains(search));
        searchCondition.or(this.locationContains(search));

        List<TrainerInfo> trainerInfoList = queryFactory
                .selectFrom(qTrainerInfo)
                .join(qTrainerInfo.member, qMember).fetchJoin()
                .where(
                        searchCondition
                )
                .orderBy(
                        searchOrderBy(pageRequest.getSort())
                )
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .fetch();

        // Page 설정에 사용됨,
        JPAQuery<Long> count = queryFactory
                .select(qTrainerInfo.count())
                .from(qTrainerInfo)
                .where(
                        searchCondition
                );

        return PageableExecutionUtils.getPage(trainerInfoList, pageRequest, count::fetchOne);
    }

    @Override
    public Optional<Member> findMemberByIdWithTrainerInfo(Long id) {

        Member member = queryFactory.selectFrom(qMember)
                .where(
                        qMember.id.eq(id),
                        qMember.role.eq(Role.TRAINER)
                )
                .leftJoin(qMember.trainerInfo, qTrainerInfo).fetchJoin()
                .fetchFirst();

        if (member == null)
            return Optional.empty();

        return Optional.of(member);
    }

    @Override
    public OptionalLong findTrainerInfoIdByMemberEmail(String email) {

        Long trainerInfoId =  queryFactory
                .select(qTrainerInfo.id)
                .from(qMember)
                .innerJoin(qMember.trainerInfo, qTrainerInfo)
                .where(qMember.email.eq(email))
                .fetchFirst();

        if (trainerInfoId == null)
            return OptionalLong.empty();

        return OptionalLong.of(trainerInfoId);
    }

    private BooleanExpression gymContains(String gymName) {
        return (gymName != null) ? qTrainerInfo.gymName.like("%"+gymName+"%") : null;
    }

    private BooleanExpression trainerNameContains(String trainerName) {
        return (trainerName != null) ? qMember.name.like("%"+trainerName+"%") : null;
    }

    private BooleanExpression locationContains(String location) {
        return (location != null) ? qMember.address.like("%"+location+"%") : null;
    }

    private OrderSpecifier<?> searchOrderBy(Sort sort) {

        Sort.Order alphaOrder = sort.getOrderFor("alpha");
        Sort.Order reviewOrder = sort.getOrderFor("review");

        if (alphaOrder != null) {
            // 알파벳 순
            StringPath alpha = qMember.name;
            if (alphaOrder.getDirection().isDescending())
                return alpha.desc();
            return alpha.asc();
        } else if (reviewOrder != null) {
            // 리뷰 점수 순
            NumberPath<Double> reviewScore = qTrainerInfo.reviewScore;
            if (reviewOrder.isDescending())
                return reviewScore.desc();
            return reviewScore.asc();
        }

        return null;
    }


}
