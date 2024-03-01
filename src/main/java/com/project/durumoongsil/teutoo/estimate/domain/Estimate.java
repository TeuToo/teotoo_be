package com.project.durumoongsil.teutoo.estimate.domain;


import com.project.durumoongsil.teutoo.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.FetchType.*;
import static lombok.AccessLevel.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor(access = PROTECTED)
public class Estimate{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long price;
    private Integer ptCount;
    private String ptAddress;

    @ManyToOne(fetch = LAZY, cascade = ALL)
    @ToString.Exclude
    private Member member;

    @Builder
    public Estimate(Long id, Long price, Integer ptCount, String ptAddress, Member member) {
        this.id = id;
        this.price = price;
        this.ptCount = ptCount;
        this.ptAddress = ptAddress;
        this.member = member;
    }
}
