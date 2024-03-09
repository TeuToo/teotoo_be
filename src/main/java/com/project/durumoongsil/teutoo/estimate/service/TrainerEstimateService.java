package com.project.durumoongsil.teutoo.estimate.service;

import com.project.durumoongsil.teutoo.common.RestResult;
import com.project.durumoongsil.teutoo.estimate.domain.TrainerEstimate;
import com.project.durumoongsil.teutoo.estimate.dto.trainer.CreateTrainerEstimateDto;
import com.project.durumoongsil.teutoo.estimate.dto.user.UpdateEstimateDto;
import com.project.durumoongsil.teutoo.estimate.repository.TrainerEstimateRepository;
import com.project.durumoongsil.teutoo.exception.NotFoundUserException;
import com.project.durumoongsil.teutoo.member.domain.Member;
import com.project.durumoongsil.teutoo.member.repository.MemberRepository;
import com.project.durumoongsil.teutoo.trainer.ptprogram.domain.PtProgram;
import com.project.durumoongsil.teutoo.trainer.ptprogram.repository.PtProgramRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TrainerEstimateService {

    private final TrainerEstimateRepository trainerEstimateRepository;
    private final MemberRepository memberRepository;
    private final PtProgramRepository ptProgramRepository;

    public List<Member>  getPtProgramsAndName(String currentLoginId) {
        List<Member> allPtProgramByEmail = memberRepository.findAllPtProgramByEmail(currentLoginId);
        return allPtProgramByEmail;
    }

    public void createTrainerEstimate(CreateTrainerEstimateDto createEstimateDto) {
        PtProgram ptProgram = getPtProgram(createEstimateDto.getPtProgram().getId());
        TrainerEstimate trainerEstimate = TrainerEstimate
                .builder()
                .price(createEstimateDto.getPrice())
                .ptCenterAddress(createEstimateDto.getPtAddress())
                .member(ptProgram.getTrainerInfo().getMember())
                .ptProgram(ptProgram)
                .build();
        trainerEstimateRepository.save(trainerEstimate);
    }

    public TrainerEstimate searchTrainerEstimate(Long estimateId) {
        return trainerEstimateRepository.findByPtProgramIdWithFetch(estimateId);
    }

    public Object updateTrainerEstimate(Long estimateId, UpdateEstimateDto updateEstimateDto) {
        return null;
    }

    public void deleteTrainerEstimate(Long estimateId) {
        trainerEstimateRepository.deleteById(estimateId);
    }

    private Member getLoginMember(String loginUserEmail) {
        return memberRepository.findMemberByEmail(loginUserEmail).orElseThrow(()->new NotFoundUserException("회원이 없습니다."));
    }

    private PtProgram getPtProgram(Long trainerEstimateId) {
        return ptProgramRepository.findById(trainerEstimateId).orElseThrow(()-> new IllegalStateException("프로그램이 없습니다."));
    }
}
