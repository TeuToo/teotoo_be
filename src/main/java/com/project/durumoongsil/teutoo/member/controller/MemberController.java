package com.project.durumoongsil.teutoo.member.controller;

import com.project.durumoongsil.teutoo.common.RestResult;
import com.project.durumoongsil.teutoo.exception.UserUnauthorizedException;
import com.project.durumoongsil.teutoo.member.dto.MemberJoinDto;
import com.project.durumoongsil.teutoo.member.dto.MemberUpdateDto;
import com.project.durumoongsil.teutoo.member.service.front.MemberFrontService;
import com.project.durumoongsil.teutoo.security.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Tag(name = "회원 관련 API")
@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberFrontService memberFrontService;

    @Operation(summary = "회원가입 API",
            description = "유저, 트레이너가 처음 회원가입 할때 사용합니다.",
            requestBody = @RequestBody(content = @Content(mediaType = "multipart/form-data",
                    schema = @Schema(implementation = MemberJoinDto.class)))
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 가입 성공"),
            @ApiResponse(responseCode = "400", description = "클라이언트의 잘못된 요청")
    })
    @PostMapping("/join")
    public RestResult join(@Validated MemberJoinDto memberJoinDto) {
        return memberFrontService.signUpResultResult(memberJoinDto);
    }


    @Operation(summary = "회원 정보 조회 (단건)", description = "회원이 자신의 정보를 조회 할때 자기 자신의 정보 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 조회 성공")
    })
    @GetMapping("/members/me")
    public RestResult findMember() {
        return memberFrontService.findMemberData(getLoginUserEmail());
    }


    @Operation(summary = "멤버 정보 수정 API",
            description = "회원가입, 로그인 한 유저의 정보를 수정하는데 사용합니다.",
            requestBody = @RequestBody(content = @Content(mediaType = "multipart/form-data",
                    schema = @Schema(implementation = MemberUpdateDto.class)))
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 가입 성공"),
            @ApiResponse(responseCode = "403", description = "권한이 없는 유저의 요청")
    })
    @PatchMapping("/members/me")
    public RestResult updateMemberInfo(@Validated MemberUpdateDto memberUpdateDto) {
        return memberFrontService.updateInfoResult(getLoginUserEmail(), memberUpdateDto);
    }

    /**
     * 스프링 시큐리티 + jwt 토큰을 통해서 현재 인증된 사용자의 아이디(email)를 가져온다.
     */
    private String getLoginUserEmail() {
        return SecurityUtil.getCurrentLoginId().orElseThrow(() ->
                new UserUnauthorizedException("인증 권한이 없습니다."));
    }
}
