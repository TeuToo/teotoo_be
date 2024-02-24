package com.project.durumoongsil.teutoo.member.controller;

import com.project.durumoongsil.teutoo.common.RestResult;
import com.project.durumoongsil.teutoo.exception.UserUnauthorizedException;
import com.project.durumoongsil.teutoo.member.domain.Member;
import com.project.durumoongsil.teutoo.member.dto.MemberJoinDto;
import com.project.durumoongsil.teutoo.member.dto.MemberUpdateDto;
import com.project.durumoongsil.teutoo.member.service.MemberService;
import com.project.durumoongsil.teutoo.security.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Tag(name = "회원 관련 API")
@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "회원가입 API", description = "유저, 트레이너가 처음 회원가입 할때 사용합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 가입 성공"),
            @ApiResponse(responseCode = "400", description = "클라이언트의 잘못된 요청")
    })
    @PostMapping( "/join")
    public ResponseEntity<String> join(@ParameterObject @Validated MemberJoinDto memberJoinDto) {
        memberService.signUp(memberJoinDto);
        return ResponseEntity.ok("회원가입 성공");
    }

    @Operation(summary = "멤버 정보 수정 API", description = "회원가입, 로그인 한 유저의 정보를 수정하는데 사용합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 가입 성공"),
            @ApiResponse(responseCode = "403", description = "권한이 없는 유저의 요청")
    })
    @PatchMapping("/members/me")
    public RestResult updateMemberInfo(@ParameterObject @Validated MemberUpdateDto memberUpdateDto) {
        String userEmail = SecurityUtil.getCurrentLoginId().orElseThrow(() ->
                new UserUnauthorizedException("인증 권한이 없습니다."));

        Member updateMember = memberService.updateInfo(userEmail, memberUpdateDto);
        return new RestResult(updateMember);
    }
}
