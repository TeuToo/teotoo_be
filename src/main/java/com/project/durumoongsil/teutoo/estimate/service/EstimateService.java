package com.project.durumoongsil.teutoo.estimate.service;

import com.project.durumoongsil.teutoo.estimate.domain.Estimate;
import com.project.durumoongsil.teutoo.estimate.dto.CreateEstimateDto;
import com.project.durumoongsil.teutoo.estimate.repository.EstimateRepository;
import com.project.durumoongsil.teutoo.exception.DuplicateEstimateException;
import com.project.durumoongsil.teutoo.member.domain.Member;
import com.project.durumoongsil.teutoo.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EstimateService {

    private final EstimateRepository estimateRepository;
    private final MemberRepository memberRepository;

    /**
     * 우선 기획상 한사람당 한개의 견적사만 할 수 있다.
     */
    public void createEstimate(CreateEstimateDto createEstimateDto, String loginUserEmail) {
        Member member = memberRepository.findMemberByEmail(loginUserEmail).get();
        isEstimateAvailable(member);
        estimateRepository.save(createEstimateEntity(createEstimateDto, member));
    }

    private Estimate createEstimateEntity(CreateEstimateDto createEstimateDto, Member member) {
        return Estimate.builder()
                .price(createEstimateDto.getPrice())
                .ptAddress(createEstimateDto.getPtAddress())
                .ptCount(createEstimateDto.getPtCount())
                .member(member)
                .build();
    }

    private void isEstimateAvailable(Member member) {
        if (estimateRepository.countEstimateByMember(member) > 0) {
            throw new DuplicateEstimateException("이미 작성한 견적서가 있습니다.");
        }
    }
}
