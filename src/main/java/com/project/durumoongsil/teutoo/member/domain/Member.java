package com.project.durumoongsil.teutoo.member.domain;


import com.project.durumoongsil.teutoo.common.BaseTimeEntity;
import com.project.durumoongsil.teutoo.member.dto.MemberJoinDto;
import com.project.durumoongsil.teutoo.member.dto.MemberUpdateDto;
import com.project.durumoongsil.teutoo.trainer.domain.TrainerInfo;
import jakarta.persistence.*;
import lombok.*;

import static lombok.AccessLevel.*;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String email;
    @Setter
    private String password;
    private String address;

    @Enumerated(EnumType.STRING)
    @Setter
    private Role role;

    private String profileImageName;
    private String profileOriginalImageName;

    @OneToOne(mappedBy = "member", fetch = FetchType.LAZY)
    @Setter
    private TrainerInfo trainerInfo;

    @Builder
    public Member(Long id, String name, String email, String password, String address,
                  Role role, String profileImagePath, String profileImageName) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.address = address;
        this.role = role;
        this.profileImageName = profileImagePath;
        this.profileOriginalImageName = profileImageName;
    }

    public static Member toEntity(MemberJoinDto memberJoinDto) {
        return Member.builder()
                .name(memberJoinDto.getName())
                .email(memberJoinDto.getEmail())
                .password(memberJoinDto.getPassword())
                .address(memberJoinDto.getAddress())
                .build();
    }

    public Member updateInfo(MemberUpdateDto memberUpdateDto) {
        this.address = memberUpdateDto.getAddress();
        return this;
    }

    public void setProfileImageAndPath(String imagePath, String imageName) {
        this.profileOriginalImageName = imageName;
        this.profileImageName = imagePath;
    }
}
