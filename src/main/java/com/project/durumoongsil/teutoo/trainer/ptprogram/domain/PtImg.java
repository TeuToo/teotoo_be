package com.project.durumoongsil.teutoo.trainer.ptprogram.domain;

import com.project.durumoongsil.teutoo.common.domain.File;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PtImg {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id3")
    private TrainerProgram trainerProgram;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id2")
    private File file;

    public PtImg(TrainerProgram trainerProgram, File file) {
        this.trainerProgram = trainerProgram;
        this.file = file;
    }
}
