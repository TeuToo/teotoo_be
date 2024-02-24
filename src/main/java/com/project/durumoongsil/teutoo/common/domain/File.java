package com.project.durumoongsil.teutoo.common.domain;

import com.project.durumoongsil.teutoo.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
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

    public File(String filePath, String fileName) {
        this.filePath = filePath;
        this.fileName = fileName;
    }
}
