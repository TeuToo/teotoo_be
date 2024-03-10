package com.project.durumoongsil.teutoo.member.repository;

import com.project.durumoongsil.teutoo.member.domain.Member;
import com.project.durumoongsil.teutoo.member.repository.query.MemberQueryRepository;
import com.project.durumoongsil.teutoo.trainer.ptprogram.domain.PtProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member,Long>, MemberQueryRepository {
    Optional<Member> findMemberByEmail(String email);
}
