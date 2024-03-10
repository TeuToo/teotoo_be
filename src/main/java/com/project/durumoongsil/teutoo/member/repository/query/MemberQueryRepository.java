package com.project.durumoongsil.teutoo.member.repository.query;

import com.project.durumoongsil.teutoo.member.domain.Member;

import java.util.List;

public interface MemberQueryRepository {
    List<Member> findAllPtProgramByEmail(String email);
}
