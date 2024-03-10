package com.project.durumoongsil.teutoo.estimate.service;

import com.project.durumoongsil.teutoo.common.LoginEmail;
import com.project.durumoongsil.teutoo.estimate.domain.Estimate;
import com.project.durumoongsil.teutoo.estimate.domain.TrainerEstimate;
import com.project.durumoongsil.teutoo.estimate.dto.trainer.CreateTrainerEstimateDto;
import com.project.durumoongsil.teutoo.estimate.dto.trainer.UpdateTrainerEstimateDto;
import com.project.durumoongsil.teutoo.estimate.repository.EstimateRepository;
import com.project.durumoongsil.teutoo.estimate.repository.TrainerEstimateRepository;
import com.project.durumoongsil.teutoo.exception.NotFoundUserException;
import com.project.durumoongsil.teutoo.exception.UnauthorizedActionException;
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
    private final EstimateRepository estimateRepository;

    /**
     *  처음 트레이너가 견적석, 신청서 버튼을 눌렀을대 이름은 그냥 표기, 프로그램 select 박스로 하기 위해서
     *  @GetMapping("/estimates/programs")
     */
    @Transactional(readOnly = true)
    public List<Member>  getPtProgramsAndName(String currentLoginId) {
        return memberRepository.findAllPtProgramByEmail(currentLoginId);
    }


    /**
     * 신청서, 견적서 save
     * @PostMapping("/estimates")
     * @param createEstimateDto pt 가격, pt 주소, pt 프로그램 Id (select 박스 라서 value 로 program Id 가 넘어올거임)
     */
    public void createTrainerEstimate(CreateTrainerEstimateDto createEstimateDto) {
        PtProgram ptProgram = getPtProgram(createEstimateDto.getProgramId());
        TrainerEstimate trainerEstimate = TrainerEstimate
                .builder()
                .price(createEstimateDto.getPrice())
                .ptCenterAddress(createEstimateDto.getPtAddress())
                .member(ptProgram.getTrainerInfo().getMember())
                .ptProgram(ptProgram)
                .build();
        trainerEstimateRepository.save(trainerEstimate);
    }

    /**
     * 트레이너 입장에서 견적서를 들어감녀 일반 유저의 견적서, 신청서가 보여야한다.
     * 여기서는 No-offset 사용
     */
    @Transactional(readOnly = true)
    public List<Estimate> searchAllUserEstimate(Long cursorId, int size) {
        return estimateRepository.findEstimateAfterCursor(cursorId, size);
    }

    /**
     * 사용자 입장에서 프로그램 단건조회
     * @GetMapping("/estimates")
     * @param estimateId 신청서 ID
     */
    @Transactional(readOnly = true)
    public TrainerEstimate searchTrainerEstimate(Long estimateId) {
        return trainerEstimateRepository.findByPtProgramIdWithFetch(estimateId);
    }

    /**
     * 트레이너가 견적서, 신청서 수정할때 사용
     * @PatchMapping("/estimates/{estimateId}")
     * @param estimateId 신청서 Id
     * @param updateEstimateDto pt 가격, pt 주소, pt 프로그램 Id
     * @return 수정된 트레이너 견적서 객체
     */
    public TrainerEstimate updateTrainerEstimate(Long estimateId, UpdateTrainerEstimateDto updateEstimateDto) {
        TrainerEstimate trainerEstimate = hasAuthority(estimateId, LoginEmail.getLoginUserEmail());
        if (updateEstimateDto.getProgramId() != null) {
            PtProgram ptProgram = ptProgramRepository.findById(updateEstimateDto.getProgramId()).orElseThrow(() -> new IllegalStateException("프로그램이 없습니다."));
            trainerEstimate.setPtProgram(ptProgram);
        }
        trainerEstimate.setPrice(updateEstimateDto.getPrice());
        trainerEstimate.setPtCenterAddress(updateEstimateDto.getPtAddress());
        return trainerEstimate;
    }


    /**
     * 트레이너 견적서 삭제
     * @DeleteMapping("/estimates/{estimateId}")
     */
    public void deleteTrainerEstimate(Long estimateId) {
        hasAuthority(estimateId, LoginEmail.getLoginUserEmail());
        trainerEstimateRepository.deleteById(estimateId);
    }

    private Member getLoginMember(String loginUserEmail) {
        return memberRepository.findMemberByEmail(loginUserEmail).orElseThrow(()->new NotFoundUserException("회원이 없습니다."));
    }

    private PtProgram getPtProgram(Long trainerEstimateId) {
        return ptProgramRepository.findById(trainerEstimateId).orElseThrow(()-> new IllegalStateException("프로그램이 없습니다."));
    }

    private TrainerEstimate hasAuthority(Long estimateId, String loginUserEmail) {
        TrainerEstimate trainerEstimate = trainerEstimateRepository.findByEstimateIdWithMember(estimateId).orElseThrow(()-> new IllegalStateException("견적서가 없습니다."));
        if (!trainerEstimate.getMember().getEmail().equals(loginUserEmail)) {
            throw new UnauthorizedActionException("권한이 없습니다");
        }
        return trainerEstimate;
    }
}
