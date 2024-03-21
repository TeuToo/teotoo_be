package com.project.durumoongsil.teutoo.trainer.ptprogram.service;

import com.project.durumoongsil.teutoo.chat.domain.Chat;
import com.project.durumoongsil.teutoo.exception.*;
import com.project.durumoongsil.teutoo.member.domain.Member;
import com.project.durumoongsil.teutoo.member.repository.MemberRepository;
import com.project.durumoongsil.teutoo.security.service.SecurityService;
import com.project.durumoongsil.teutoo.trainer.ptprogram.constants.ReservationStatus;
import com.project.durumoongsil.teutoo.trainer.ptprogram.domain.PtProgram;
import com.project.durumoongsil.teutoo.trainer.ptprogram.domain.PtReservation;
import com.project.durumoongsil.teutoo.trainer.ptprogram.dto.request.PtAcceptReqDto;
import com.project.durumoongsil.teutoo.trainer.ptprogram.dto.request.PtReservationReqDto;
import com.project.durumoongsil.teutoo.trainer.ptprogram.dto.response.PtAcceptResDto;
import com.project.durumoongsil.teutoo.trainer.ptprogram.dto.response.PtReservationResDto;
import com.project.durumoongsil.teutoo.trainer.ptprogram.repository.PtProgramRepository;
import com.project.durumoongsil.teutoo.trainer.ptprogram.repository.PtReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PtReservationService {

    private final PtReservationRepository ptReservationRepository;
    private final PtProgramRepository ptProgramRepository;
    private final SecurityService securityService;
    private final MemberRepository memberRepository;

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

        Member member = this.getMember();

        PtReservation ptReservation = ptReservationRepository.findByIdWithMemberAndPtProgram(ptAcceptReqDto.getReservationId())
                .orElseThrow(() -> new PtReservationNotFoundException("해당 예약 정보를 찾을 수 없습니다."));

        if (!Objects.equals(member.getId(), ptReservation.getMember().getId())) {
            throw new UnauthorizedActionException();
        }

        if (ptReservation.getStatus() != ReservationStatus.PENDING) {
            throw new InvalidActionException();
        }

        ptReservation.updateStatus(ReservationStatus.RESERVED);

        return new PtAcceptResDto(ptReservation.getId());
    }
}
