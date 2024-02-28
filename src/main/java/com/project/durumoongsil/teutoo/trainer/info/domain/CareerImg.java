package com.project.durumoongsil.teutoo.trainer.info.domain;

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
public class CareerImg {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_info_id")
    private TrainerInfo trainerInfo;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id")
    private File file;

    public CareerImg(TrainerInfo trainerInfo, File file) {
        this.trainerInfo = trainerInfo;
        this.file = file;
    }
}
