package com.project.durumoongsil.teutoo.common.domain;

import com.project.durumoongsil.teutoo.common.BaseTimeEntity;
import com.project.durumoongsil.teutoo.trainer.info.domain.CareerImg;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class File extends BaseTimeEntity {

    @Id @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private String fileName;

    // File 삭제시 CareerImg 또한 같이 삭제됨
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "file", cascade = CascadeType.REMOVE)
    private CareerImg careerImg;

    public File(String filePath, String fileName) {
        this.filePath = filePath;
        this.fileName = fileName;
    }
}
