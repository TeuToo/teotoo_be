package com.project.durumoongsil.teutoo.trainer.ptprogram.domain;

import com.project.durumoongsil.teutoo.trainer.info.domain.TrainerInfo;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PtProgram {

    @Id @GeneratedValue
    private Long id;

    private String title;

    private String content;

    private int price;

    private int ptCnt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="id2")
    private TrainerInfo trainerInfo;

    @Builder
    public PtProgram(String title, String content, int price, int ptCnt, TrainerInfo trainerInfo) {
        this.title = title;
        this.content = content;
        this.price = price;
        this.ptCnt = ptCnt;
        this.trainerInfo = trainerInfo;
    }
}
