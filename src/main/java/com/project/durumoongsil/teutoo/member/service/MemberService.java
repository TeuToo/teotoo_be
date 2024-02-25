package com.project.durumoongsil.teutoo.member.service;

import com.project.durumoongsil.teutoo.common.service.FileService;
import com.project.durumoongsil.teutoo.exception.NotFoundUserException;
import com.project.durumoongsil.teutoo.member.domain.Member;
import com.project.durumoongsil.teutoo.member.domain.Role;
import com.project.durumoongsil.teutoo.member.dto.MemberJoinDto;
import com.project.durumoongsil.teutoo.member.dto.MemberSearchDto;
import com.project.durumoongsil.teutoo.member.dto.MemberUpdateDto;
import com.project.durumoongsil.teutoo.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.project.durumoongsil.teutoo.member.domain.Role.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileService fileService;

    private static final String MEMBER_IMAGE_PATH = "member_profile";


    public void signUp(MemberJoinDto memberJoinDto) {
        Member member = Member.toEntity(memberJoinDto);

        //  회원 엔티티 세팅 (권한, 패스워드 암호화, 프로필 이미지 설정)
        String encodedPassword = passwordEncoder.encode(member.getPassword());
        member.setRole(grantRole(memberJoinDto.getSortRole()));
        member.setPassword(encodedPassword);
        setProfileImageAndPath(member,memberJoinDto.getMultipartFile());

        memberRepository.save(member);
    }

    public MemberSearchDto findMember(String userEmail) {
        Member member = memberRepository.findMemberByEmail(userEmail)
                .orElseThrow(() -> new NotFoundUserException("사용자를 찾을 수 없습니다."));

        return MemberSearchDto.builder()
                .name(member.getName())
                .address(member.getAddress())
                .profileImageName(member.getProfileImageName())
                .profileImagePath(member.getProfileImagePath())
                .build();
    }

    public Member updateInfo(String userEmail, MemberUpdateDto memberUpdateDto) {
        Member member = memberRepository.findMemberByEmail(userEmail)
                .orElseThrow(() -> new NotFoundUserException("사용자를 찾을 수 없습니다."));

        // 이미지가 있을 경우 이미지 처리
        updateProfileImage(memberUpdateDto, member);

        return member.updateInfo(memberUpdateDto);
    }

    /**
     * 회원가입 시 트레이너 입니까? 라는 체크 항목에 따라 권한 부여
     * @param role 회원가입시 체크항목 true : 트레이너, false : 일반 유저
     */
    private Role grantRole(Boolean role) {
        return role ? TRAINER : USER;
    }

    /**
     * 기존 이미지는 삭제하고, 새로운 이미지로 대체
     */
    private void updateProfileImage(MemberUpdateDto memberUpdateDto, Member member) {
        if (memberUpdateDto.getProfileImage() != null) {
            fileService.deleteImg(MEMBER_IMAGE_PATH, member.getProfileImageName());
            setProfileImageAndPath(member, memberUpdateDto.getProfileImage());
        }
    }

    /**
     * s3 랑 File 엔티티에 프로필 사진 관리
     * @param file 기본이미지 혹은, 회원이 올린 프로필 사진
     */
    private void setProfileImageAndPath(Member member, MultipartFile file) {
        try {
            String fileName = fileService.saveImg(MEMBER_IMAGE_PATH, file);
            member.setProfileImageAndPath(MEMBER_IMAGE_PATH + "/" + fileName, file.getOriginalFilename());
        } catch (IOException e) {
            throw new RuntimeException("이미지 저장에 실패했습니다. 다시 시도해 주세요");
        }
    }
}
