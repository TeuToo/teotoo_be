package com.project.durumoongsil.teutoo.member.service.front;

import com.project.durumoongsil.teutoo.common.RestResult;
import com.project.durumoongsil.teutoo.common.service.FileService;
import com.project.durumoongsil.teutoo.member.domain.Member;
import com.project.durumoongsil.teutoo.member.dto.MemberJoinDto;
import com.project.durumoongsil.teutoo.member.dto.MemberSearchDto;
import com.project.durumoongsil.teutoo.member.dto.MemberUpdateDto;
import com.project.durumoongsil.teutoo.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberFrontService {

    private final MemberService memberService;
    private final FileService fileService;

    public RestResult signUpResultResult(MemberJoinDto memberJoinDto) {
        memberService.signUp(memberJoinDto);
        return new RestResult("회원가입 성공");
    }

    public RestResult findMemberData(String loginUserEmail) {
        Member member = memberService.findMember(loginUserEmail);
        return new RestResult(createMemberSearchDto(member));
    }

    public RestResult updateInfoResult(String loginUserEmail, MemberUpdateDto memberUpdateDto) {
        Member member = memberService.updateInfo(loginUserEmail, memberUpdateDto);
        return new RestResult(createMemberSearchDto(member));
    }

    private MemberSearchDto createMemberSearchDto(Member member) {
        String MEMBER_IMAGE_PATH = "member_profile";
        return MemberSearchDto.builder()
                .memberId(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .address(member.getAddress())
                .profileImagePath(fileService.getImgUrl(MEMBER_IMAGE_PATH, member.getProfileImageName()))
                .profileImageName(member.getProfileOriginalImageName())
                .setRole(member.getRole().toString())
                .build();
    }

    /**
     * 패스워드 재발송을 위해서 입력한 이메일로 비밀번호 재설정 링크를 보낸다
     * @param email 회원가입시 입력한 이메일
     * @return "비밀번호 재설정 링크를 보냈습니다."
     */
    public RestResult startPasswordResetProcess(String email) {
        // 실제로 가입한 이메일인지 확인
        return new RestResult(memberService.sendResetLink(email));
    }
}
