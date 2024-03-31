package com.project.durumoongsil.teutoo.trainer.ptprogram.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.project.durumoongsil.teutoo.chat.domain.Chat;
import com.project.durumoongsil.teutoo.common.domain.FilePath;
import com.project.durumoongsil.teutoo.common.dto.ImgResDto;
import com.project.durumoongsil.teutoo.common.service.FileService;
import com.project.durumoongsil.teutoo.exception.*;
import com.project.durumoongsil.teutoo.member.domain.Member;
import com.project.durumoongsil.teutoo.member.domain.Role;
import com.project.durumoongsil.teutoo.member.repository.MemberRepository;
import com.project.durumoongsil.teutoo.security.service.SecurityService;
import com.project.durumoongsil.teutoo.trainer.ptprogram.constants.ReservationStatus;
import com.project.durumoongsil.teutoo.trainer.ptprogram.domain.PtProgram;
import com.project.durumoongsil.teutoo.trainer.ptprogram.domain.PtReservation;
import com.project.durumoongsil.teutoo.trainer.ptprogram.dto.request.PtAcceptReqDto;
import com.project.durumoongsil.teutoo.trainer.ptprogram.dto.request.PtReservationReqDto;
import com.project.durumoongsil.teutoo.trainer.ptprogram.dto.response.PtAcceptResDto;
import com.project.durumoongsil.teutoo.trainer.ptprogram.dto.response.PtMemberScheduleResDto;
import com.project.durumoongsil.teutoo.trainer.ptprogram.dto.response.PtReservationResDto;
import com.project.durumoongsil.teutoo.trainer.ptprogram.dto.response.PtTrainerScheduleResDto;
import com.project.durumoongsil.teutoo.trainer.ptprogram.repository.PtProgramRepository;
import com.project.durumoongsil.teutoo.trainer.ptprogram.repository.PtReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PtReservationService {

    private final PtReservationRepository ptReservationRepository;
    private final PtProgramRepository ptProgramRepository;
    private final SecurityService securityService;
    private final MemberRepository memberRepository;
    private final FileService fileService;

    @Transactional
    public PtReservationResDto reserve(PtReservationReqDto reservationReqDto) {
        Member member = this.getMember();

        PtProgram ptProgram = ptProgramRepository.findById(reservationReqDto.getProgramId())
                .orElseThrow(PtProgramNotFoundException::new);

        if (!this.canReserve(reservationReqDto.getProgramId(),
                reservationReqDto.getStartTime(), reservationReqDto.getEndTime())) {
            throw new ScheduleConflictException("선택하신 시간대는 이미 예약된 시간대 입니다.");
        }

        PtReservation savedPtReservation = this.savePtReservation(member, ptProgram,
                reservationReqDto.getStartTime(), reservationReqDto.getEndTime());

        return new PtReservationResDto(savedPtReservation.getId());
    }

    private Member getMember() {
        Member member = memberRepository.findMemberByEmail(securityService.getLoginedUserEmail())
                .orElseThrow(() -> new UsernameNotFoundException("해당 회원을 찾을 수 없습니다."));

        return member;
    }

    private PtReservation savePtReservation(Member member, PtProgram ptProgram, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        // 예약 정보 저장
        PtReservation ptReservation = PtReservation.builder()
                .ptProgram(ptProgram)
                .member(member)
                .status(ReservationStatus.PENDING)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .build();

        return ptReservationRepository.save(ptReservation);
    }

    private boolean canReserve(Long programId, LocalDateTime startTIme, LocalDateTime endTime) {
        Long reserveCnt = ptReservationRepository.countPtReservationByProgramIdANdDateTimeRange(programId, startTIme, endTime);

        return reserveCnt < 1;
    }


    @Transactional
    public PtAcceptResDto accept(PtAcceptReqDto ptAcceptReqDto) {

        Member trainer = this.getMember();

        PtReservation ptReservation = ptReservationRepository.findById(ptAcceptReqDto.getReservationId())
                .orElseThrow(() -> new PtReservationNotFoundException("해당 예약 정보를 찾을 수 없습니다."));

        Long savedTrainerId = ptReservationRepository.findTrainerIdById(ptAcceptReqDto.getReservationId())
                .orElseThrow(() -> new NotFoundUserException("해당 사용자를 찾을 수 없습니다."));

        if (!Objects.equals(trainer.getId(), savedTrainerId)) {
            throw new UnauthorizedActionException();
        }

        if (ptReservation.getStatus() != ReservationStatus.PENDING) {
            throw new InvalidActionException();
        }

        ptReservation.updateStatus(ReservationStatus.RESERVED);

        return new PtAcceptResDto(ptReservation.getId());
    }



    /**
     * 트레이너의 PT 스케쥴 리스트를 얻기 위한 메소드입니다.
     */
    public List<PtTrainerScheduleResDto> getPtProgramTrainerScheduleList() {
        Member member = this.getMember();

        if (!isTrainer(member.getRole()))
            throw new InvalidActionException("해당 사용자의 예약 스케쥴을 조회 할 수 없습니다.");

        List<PtProgram> ptProgramList = ptProgramRepository.findByTrainerIdWithPtReservation(member.getId());

        return ptProgramList.stream()
                .flatMap(ptProgram -> ptProgram.getPtReservationList().stream())
                .map(this::createPtTrainerScheduleResDto)
                .toList();
    }

    private boolean isTrainer(Role role) {
        return role == Role.TRAINER;
    }

    private PtTrainerScheduleResDto createPtTrainerScheduleResDto(PtReservation ptReservation) {
        ImgResDto imgResDto = new ImgResDto(
                ptReservation.getMember().getProfileImageName(),
                fileService.getImgUrl(
                        FilePath.MEMBER_PROFILE.getPath(),
                        ptReservation.getMember().getProfileImageName()
                )
        );

        return PtTrainerScheduleResDto.builder()
                .memberId(ptReservation.getMember().getId())
                .memberName(ptReservation.getMember().getName())
                .imgResDto(imgResDto)
                .startDateTime(ptReservation.getStartDateTime())
                .endDateTime(ptReservation.getEndDateTime())
                .build();
    }


    /**
     * 회원의 PT 스케쥴 리스트를 얻기 위한 메소드입니다.
     */
    public List<PtMemberScheduleResDto> getPtProgramMemberScheduleList() {
        Member member = this.getMember();
        if (!isMember(member.getRole()))
            throw new InvalidActionException("해당 사용자의 예약 스케쥴을 조회 할 수 없습니다.");

        List<PtReservation> ptReservationList = ptReservationRepository
                .findByMemberIdWithPtProgramAndTrainerInfoAndMember(member.getId());


        return ptReservationList.stream().map(this::createMemberScheduleDto).toList();
    }

    private boolean isMember(Role role) {
        return role == Role.USER;
    }

    private PtMemberScheduleResDto createMemberScheduleDto(PtReservation ptReservation) {
        PtProgram ptProgram = ptReservation.getPtProgram();
        Member member = ptProgram.getTrainerInfo().getMember();

        ImgResDto imgResDto = new ImgResDto(
                member.getProfileImageName(),
                fileService.getImgUrl(FilePath.MEMBER_PROFILE.getPath(), member.getProfileImageName())
        );

        return PtMemberScheduleResDto.builder()
                .trainerId(member.getId())
                .trainerName(member.getName())
                .programId(ptProgram.getId())
                .programName(ptProgram.getTitle())
                .startDateTime(ptReservation.getStartDateTime())
                .endDateTime(ptReservation.getEndDateTime())
                .status(ptReservation.getStatus())
                .imgResDto(imgResDto)
                .build();
    }



}
