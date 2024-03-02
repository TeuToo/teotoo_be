package com.project.durumoongsil.teutoo.estimate.repository.query;

import com.project.durumoongsil.teutoo.estimate.domain.Estimate;
import com.project.durumoongsil.teutoo.estimate.repository.EstimateRepository;
import com.project.durumoongsil.teutoo.member.domain.Member;
import com.project.durumoongsil.teutoo.member.repository.MemberRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class EstimateQueryRepositoryImplTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private EstimateRepository estimateRepository;
    @Autowired
    private JPAQueryFactory factory;

    @Test
    @DisplayName("견적서와 회원 페치조인")
    void estimateFetchMember() {
        Member member = Member.builder()
                .email("test@test")
                .build();
        memberRepository.save(member);


        Estimate estimate = Estimate.builder()
                .member(member)
                .build();
        estimateRepository.save(estimate);

    }

}