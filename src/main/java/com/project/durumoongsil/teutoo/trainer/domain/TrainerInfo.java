package com.project.durumoongsil.teutoo.trainer.domain;

import com.project.durumoongsil.teutoo.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@Table(name = "trainer_info")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrainerInfo {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(nullable = false)
    private String gymName;

    @Column(nullable = false)
    private String simpleIntro;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String introContent;

    @ColumnDefault("0")
    private int reviewCnt;

    @ColumnDefault("0")
    private double reviewScore;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public void updateGymName(String gymName) {
        this.gymName = gymName;
    }

    public void updateSimpleIntro(String simpleIntro) {
        this.simpleIntro = simpleIntro;
    }

    public void updateIntroContent(String introContent) {
        this.introContent = introContent;
    }

    @Builder
    public TrainerInfo(String gymName, String simpleIntro, String introContent,
                       int reviewCnt, double reviewScore, Member member) {
        this.gymName = gymName;
        this.simpleIntro = simpleIntro;
        this.introContent = introContent;
        this.reviewCnt = reviewCnt;
        this.reviewScore = reviewScore;
        this.member = member;
    }
}
