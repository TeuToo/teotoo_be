package com.project.durumoongsil.teutoo.estimate.domain;


import com.project.durumoongsil.teutoo.common.BaseMemberTimeEntity;
import com.project.durumoongsil.teutoo.common.BaseTimeEntity;
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
public class Estimate extends BaseMemberTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer price;
    private String ptAddress;

    @ManyToOne(fetch = LAZY)
    @ToString.Exclude
    private Member member;

}
