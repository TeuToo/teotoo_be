package com.project.durumoongsil.teutoo.trainer.info.repository;

import com.project.durumoongsil.teutoo.member.domain.Member;
import com.project.durumoongsil.teutoo.member.domain.QMember;
import com.project.durumoongsil.teutoo.trainer.info.domain.QTrainerInfo;
import com.project.durumoongsil.teutoo.trainer.info.domain.TrainerInfo;
import com.project.durumoongsil.teutoo.trainer.info.dto.TrainerListReqDto;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TrainerInfoCustomRepositoryImpl implements TrainerInfoCustomRepository{

    private final JPAQueryFactory queryFactory;

    QMember qMember = QMember.member;
    QTrainerInfo qTrainerInfo = QTrainerInfo.trainerInfo;

    @Override
    public Optional<Member> findMemberByIdWithTrainerInfo(String userEmail) {

        Member member = queryFactory.selectFrom(qMember)
                .where(qMember.email.eq(userEmail))
                .leftJoin(qMember.trainerInfo, qTrainerInfo).fetchJoin()
                .fetchFirst();

        if (member == null)
            return Optional.empty();

        return Optional.of(member);
    }

    @Override
    public Optional<Member> findMemberByIdWithTrainerInfo(Long id) {

        Member member = queryFactory.selectFrom(qMember)
                .where(qMember.id.eq(id))
                .leftJoin(qMember.trainerInfo, qTrainerInfo).fetchJoin()
                .fetchFirst();

        if (member == null)
            return Optional.empty();

        return Optional.of(member);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<TrainerInfo> findBySearchCondition(TrainerListReqDto trainerListReqDto) {

        PageRequest pageRequest = PageRequest.of(trainerListReqDto.getPage(), trainerListReqDto.getSize());

        List<TrainerInfo> trainerInfoList = queryFactory
                .selectFrom(qTrainerInfo)
                .join(qTrainerInfo.member, qMember).fetchJoin()
                .where(
                        gymEq(trainerListReqDto.getSearchGym()),
                        trainerNameEq(trainerListReqDto.getSearchTrainer()),
                        locationStartWith(trainerListReqDto.getSearchLocation())
                )
                .orderBy(
                        searchOrderBy(
                                trainerListReqDto.getSort(),
                                trainerListReqDto.getDirection()
                        )
                )
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .fetch();

        // Page 설정에 사용됨,
        JPAQuery<Long> count = queryFactory
                .select(qTrainerInfo.count())
                .from(qTrainerInfo)
                .where(
                        gymEq(trainerListReqDto.getSearchGym()),
                        trainerNameEq(trainerListReqDto.getSearchTrainer()),
                        locationStartWith(trainerListReqDto.getSearchLocation())
                );

        return PageableExecutionUtils.getPage(trainerInfoList, pageRequest, count::fetchOne);
    }

    private BooleanExpression gymEq(String gymName) {
        return (gymName != null) ? qTrainerInfo.gymName.eq(gymName) : null;
    }

    private BooleanExpression trainerNameEq(String trainerName) {
        return (trainerName != null) ? qMember.name.eq(trainerName) : null;
    }

    private BooleanExpression locationStartWith(String location) {
        return (location != null) ? qMember.address.startsWith(location) : null;
    }

    private OrderSpecifier<?> searchOrderBy(String sort, String direction) {

        if (sort.equals("alpha")) {
            // 알파벳 순
            StringPath alpha = qMember.name;
            if (direction.equals("desc"))
                return alpha.desc();
            return alpha.asc();
        } else if (sort.equals("review")) {
            // 리뷰 점수 순
            NumberPath<Double> reviewScore = qTrainerInfo.reviewScore;
            if (direction.equals("desc"))
                return reviewScore.desc();
            return reviewScore.asc();
        }

        return null;
    }
}
