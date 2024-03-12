package com.project.durumoongsil.teutoo.member.repository;

import com.project.durumoongsil.teutoo.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member,Long> {
    Optional<Member> findMemberByEmail(String email);

    @Query("select m.id from Member m where m.email = :email")
    Optional<Long> findIdByEmail(@Param("email") String email);
}
