package com.project.durumoongsil.teutoo.member.repository;

import com.project.durumoongsil.teutoo.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member,Long> {
}
