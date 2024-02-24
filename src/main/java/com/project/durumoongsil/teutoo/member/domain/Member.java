package com.project.durumoongsil.teutoo.member.domain;


import com.project.durumoongsil.teutoo.common.BaseTimeEntity;
import com.project.durumoongsil.teutoo.member.dto.MemberJoinDto;
import com.project.durumoongsil.teutoo.trainer.domain.TrainerInfo;
import jakarta.persistence.*;
import lombok.*;

import static lombok.AccessLevel.*;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String password;
    private String address;

    @Enumerated(EnumType.STRING)
    @Setter
    private Role role;

    private String profile_image_path;
    private String profile_image_name;

    @OneToOne(mappedBy = "member", fetch = FetchType.LAZY)
    @Setter
    private TrainerInfo trainerInfo;

    @Builder
    public Member(Long id, String name, String email, String password, String address,
                  Role role, String profile_image_path, String profile_image_name) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.address = address;
        this.role = role;
        this.profile_image_path = profile_image_path;
        this.profile_image_name = profile_image_name;
    }

    public static Member toEntity(MemberJoinDto memberJoinDto) {
        return Member.builder()
                .name(memberJoinDto.getName())
                .email(memberJoinDto.getEmail())
                .password(memberJoinDto.getPassword())
                .address(memberJoinDto.getAddress())
                .build();
    }
}
