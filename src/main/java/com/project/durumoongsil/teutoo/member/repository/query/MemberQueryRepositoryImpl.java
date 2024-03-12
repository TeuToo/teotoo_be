package com.project.durumoongsil.teutoo.member.repository.query;

import com.project.durumoongsil.teutoo.member.domain.Member;
import com.project.durumoongsil.teutoo.trainer.info.domain.QTrainerInfo;
import com.project.durumoongsil.teutoo.trainer.ptprogram.domain.QPtProgram;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import static com.project.durumoongsil.teutoo.member.domain.QMember.*;
import static com.project.durumoongsil.teutoo.trainer.info.domain.QTrainerInfo.*;
import static com.project.durumoongsil.teutoo.trainer.ptprogram.domain.QPtProgram.*;

import java.util.List;

@RequiredArgsConstructor
public class MemberQueryRepositoryImpl implements MemberQueryRepository{

    private final JPAQueryFactory factory;
    @Override
    public List<Member> findAllPtProgramByEmail(String email) {
        return factory.select(member).distinct()
                .from(member)
                .join(member.trainerInfo, trainerInfo).fetchJoin()
                .join(trainerInfo.ptProgramList, ptProgram).fetchJoin()
                .where(member.email.eq(email))
                .fetch();
    }
}
