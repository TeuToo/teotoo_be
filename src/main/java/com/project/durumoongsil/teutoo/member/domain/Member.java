package com.project.durumoongsil.teutoo.member.domain;


import com.project.durumoongsil.teutoo.common.BaseTimeEntity;
import com.project.durumoongsil.teutoo.member.dto.MemberJoinDto;
import com.project.durumoongsil.teutoo.member.dto.MemberUpdateDto;
import com.project.durumoongsil.teutoo.trainer.info.domain.TrainerInfo;
import jakarta.persistence.*;
import lombok.*;

import static lombok.AccessLevel.*;

@Entity
@Getter
@Setter
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
    private Role role;
    private String profileImageName;
    private String profileOriginalImageName;
    @OneToOne(mappedBy = "member", fetch = FetchType.LAZY, optional = false)
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
        //기존에는 역할 바꾸기가 가능했지만 회의 결과 바꾸면 안되는결로 변경(오직 회원가입할때만 입력 가능)
        //this.role = (memberUpdateDto.getRole() ? Role.TRAINER : Role.USER);
        return this;
    }

    public void setProfileImageAndPath(String imagePath, String imageName) {
        this.profileOriginalImageName = imageName;
        this.profileImageName = imagePath;
    }
}
