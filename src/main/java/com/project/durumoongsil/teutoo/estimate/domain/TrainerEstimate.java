package com.project.durumoongsil.teutoo.estimate.domain;

import com.project.durumoongsil.teutoo.member.domain.Member;
import com.project.durumoongsil.teutoo.trainer.ptprogram.domain.PtProgram;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.java.Log;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrainerEstimate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int price;
    private String ptCenterAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;
    @ManyToOne(fetch = FetchType.LAZY)
    private PtProgram ptProgram;

    @Builder
    public TrainerEstimate(Long id, int price, String ptCenterAddress, Member member, PtProgram ptProgram) {
        this.id = id;
        this.price = price;
        this.ptCenterAddress = ptCenterAddress;
        this.member = member;
        this.ptProgram = ptProgram;
    }
}
