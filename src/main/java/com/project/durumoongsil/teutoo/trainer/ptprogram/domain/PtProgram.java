package com.project.durumoongsil.teutoo.trainer.ptprogram.domain;

import com.project.durumoongsil.teutoo.trainer.info.domain.TrainerInfo;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PtProgram {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    private int price;

    private LocalTime availableStartTime;

    private LocalTime availableEndTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="id2")
    private TrainerInfo trainerInfo;

    @OneToMany(mappedBy = "ptProgram", fetch = FetchType.LAZY)
    private List<PtImg> ptImgList;

    @OneToMany(mappedBy = "ptProgram", fetch = FetchType.LAZY)
    private List<PtReservation> ptReservationList;

    @Builder
    public PtProgram(String title, String content, int price, LocalTime availableStartTime,
                     LocalTime availableEndTime, TrainerInfo trainerInfo) {
        this.title = title;
        this.content = content;
        this.price = price;
        this.availableStartTime = availableStartTime;
        this.availableEndTime = availableEndTime;
        this.trainerInfo = trainerInfo;
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void updatePrice(int price) {
        this.price = price;
    }

    public void updateAvailableStartTime(LocalTime availableStartTime) {
        this.availableStartTime = availableStartTime;
    }

    public void updateAvailableEndTime(LocalTime availableEndTime) {
        this.availableEndTime = availableEndTime;
    }
}
