package com.project.durumoongsil.teutoo.trainer.ptprogram.domain;

import com.project.durumoongsil.teutoo.member.domain.Member;
import com.project.durumoongsil.teutoo.trainer.ptprogram.constants.ReservationStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PtReservation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id")
    private PtProgram ptProgram;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public void updateStatus(ReservationStatus status) {
        this.status = status;
    }

    @Builder
    public PtReservation(ReservationStatus status, LocalDateTime startDateTime,
                         LocalDateTime endDateTime, PtProgram ptProgram, Member member) {
        this.status = status;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.ptProgram = ptProgram;
        this.member = member;
    }
}
