package com.project.durumoongsil.teutoo.estimate.service;

import com.project.durumoongsil.teutoo.estimate.domain.TrainerEstimate;
import com.project.durumoongsil.teutoo.estimate.dto.trainer.CreateTrainerEstimateDto;
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

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TrainerEstimateService {

    private final TrainerEstimateRepository trainerEstimateRepository;
    private final MemberRepository memberRepository;
    private final PtProgramRepository ptProgramRepository;

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

    // TODO fetchJoin
    public TrainerEstimate searchTrainerEstimate(Long estimateId) {
        return trainerEstimateRepository.findById(estimateId).orElseThrow(()-> new IllegalStateException("견적서가 없습니다."));
    }

    public Object updateTrainerEstimate() {
        return null;
    }

    public void deleteTrainerEstimate(Long estimateId) {
        trainerEstimateRepository.deleteById(estimateId);
    }

    /**
     * 트레이너의 프로그램을 가져온다.
     */
    private void getTrainerProgram(Member member) {

    }

    private Member getLoginMember(String loginUserEmail) {
        return memberRepository.findMemberByEmail(loginUserEmail).orElseThrow(()->new NotFoundUserException("회원이 없습니다."));
    }

    private PtProgram getPtProgram(Long trainerEstimateId) {
        return ptProgramRepository.findById(trainerEstimateId).orElseThrow(()-> new IllegalStateException("프로그램이 없습니다."));
    }
}
