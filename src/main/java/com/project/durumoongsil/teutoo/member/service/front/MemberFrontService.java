package com.project.durumoongsil.teutoo.member.service.front;

import com.project.durumoongsil.teutoo.common.RestResult;
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
    private final ModelMapper modelMapper;

    public RestResult signUpResultResult(MemberJoinDto memberJoinDto) {
        memberService.signUp(memberJoinDto);
        return new RestResult("회원가입 성공");
    }

    public RestResult findMemberData(String loginUserEmail) {
        return new RestResult(modelMapper.map(memberService.findMember(loginUserEmail), MemberSearchDto.class));
    }

    public RestResult updateInfoResult(String loginUserEmail, MemberUpdateDto memberUpdateDto) {
        return new RestResult(modelMapper.map(memberService.updateInfo(loginUserEmail, memberUpdateDto), MemberSearchDto.class));
    }
}
