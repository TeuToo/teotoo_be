package com.project.durumoongsil.teutoo.member.service;

import com.project.durumoongsil.teutoo.common.service.EmailService;
import com.project.durumoongsil.teutoo.common.service.FileService;
import com.project.durumoongsil.teutoo.exception.DuplicateEmailException;
import com.project.durumoongsil.teutoo.exception.MailSendException;
import com.project.durumoongsil.teutoo.exception.NotFoundUserException;
import com.project.durumoongsil.teutoo.member.domain.Member;
import com.project.durumoongsil.teutoo.member.domain.Role;
import com.project.durumoongsil.teutoo.member.dto.MemberJoinDto;
import com.project.durumoongsil.teutoo.member.dto.MemberUpdateDto;
import com.project.durumoongsil.teutoo.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static com.project.durumoongsil.teutoo.member.domain.Role.*;
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileService fileService;
    private final EmailService emailService;

    private final String MEMBER_IMAGE_PATH = "member_profile";


    public void signUp(MemberJoinDto memberJoinDto) {
        // 이메일 중복 확인
        checkEmailAvailable(memberJoinDto.getEmail());

        Member member = Member.toEntity(memberJoinDto);

        //  회원 엔티티 세팅 (권한, 패스워드 암호화, 프로필 이미지 설정)
        String encodedPassword = passwordEncoder.encode(member.getPassword());
        member.setRole(grantRole(memberJoinDto.getSortRole()));
        member.setPassword(encodedPassword);
        setProfileImageAndPath(member, memberJoinDto.getProfileImage());

        memberRepository.save(member);
    }

    @Transactional(readOnly = true)
    public Member findMember(String userEmail) {
        return getMember(userEmail,"사용자를 찾을 수 없습니다.");
    }

    public Member updateInfo(String userEmail, MemberUpdateDto memberUpdateDto) {
        Member member = getMember(userEmail,"사용자를 찾을 수 없습니다.");

        updateProfileImage(memberUpdateDto, member);
        updatePassword(memberUpdateDto.getPassword(), member);
        return member.updateInfo(memberUpdateDto);
    }

    /**
     * 회원가입시 설정한 이메일로 비밀번호 재설정 링크 보냄
     * @param email 회원가입시 입력한 비밀번호
     * @return "비밀번호 재설정 링크가 이메일로 전송되었습니다."
     */
    public String sendResetLink(String email) {
        Member member = checkSignUpMember(email);
        String tempPassword = createTempPassword(member);
        try {
            emailService.sendMail(email, tempPassword);
            return "비밀번호 재설정 링크가 이메일로 전송되었습니다.";
        } catch (Exception e) {
            throw new MailSendException("이메일 발송 실패");
        }
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
            fileService.deleteImg(MEMBER_IMAGE_PATH, member.getProfileOriginalImageName());
            setProfileImageAndPath(member, memberUpdateDto.getProfileImage());
        }
    }


    /**
     * s3 랑 File 엔티티에 프로필 사진 관리
     * @param file 기본이미지 혹은, 회원이 올린 프로필 사진
     */
    private void setProfileImageAndPath(Member member, MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            String fileName = fileService.saveImg(MEMBER_IMAGE_PATH, file);
            member.setProfileImageAndPath(fileName, file.getOriginalFilename());
        }
    }


    private Member checkSignUpMember(String email) {
        return getMember(email, "유효한 이메일이 아닙니다.");
    }

    private void checkEmailAvailable(String email) {
        if (memberRepository.findMemberByEmail(email).isPresent()) {
            throw new DuplicateEmailException("이미 가입한 이메일 입니다.");
        }
    }


    /**
     * 임시 비밀번호 생성 UUID 로 8자리 생성
     * @return 임시 비밀번호
     */
    private String createTempPassword(Member member) {
        // UUID를 생성하고 문자열로 변환
        String uuid = UUID.randomUUID().toString();

        // 하이픈(-)을 제거하고, 8글자로 자름
        String tempPassword = uuid.replace("-", "").substring(0, 8);

        member.setPassword(passwordEncoder.encode(tempPassword));
        return tempPassword;
    }

    private Member getMember(String userEmail, String errorMsg) {
        return memberRepository.findMemberByEmail(userEmail)
                .orElseThrow(() -> new NotFoundUserException(errorMsg));
    }

    private void updatePassword(String password, Member member) {
        member.setPassword(passwordEncoder.encode(password));
    }
}
