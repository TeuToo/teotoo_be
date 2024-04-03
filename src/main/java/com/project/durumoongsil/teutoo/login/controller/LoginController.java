package com.project.durumoongsil.teutoo.login.controller;

import com.project.durumoongsil.teutoo.login.dto.KakaoInfo;
import com.project.durumoongsil.teutoo.login.dto.LoginDto;
import com.project.durumoongsil.teutoo.login.dto.TokenDto;
import com.project.durumoongsil.teutoo.login.service.OauthService;
import com.project.durumoongsil.teutoo.security.jwt.JwtAuthenticationFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "로그인 API")
@RequestMapping("/login")
@RestController
@RequiredArgsConstructor
public class LoginController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final OauthService oauthService;

    @Operation(summary = "로그인",
            description = "이메일과 비밀번호를 사용하여 로그인합니다.",
            requestBody = @RequestBody(content = @Content(mediaType = "application/x-www-form-urlencoded",
                    schema = @Schema(implementation = LoginDto.class))))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 토큰 발급"),
            @ApiResponse(responseCode = "401", description = "로그인 실패"),
            @ApiResponse(responseCode = "403", description = "권한이 없는 오류")
    })
    @PostMapping
    public ResponseEntity<TokenDto> authorize(@Validated LoginDto loginDto) {
        log.info("loginDto ={}", loginDto);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = oauthService.createToken(authentication);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtAuthenticationFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);

        return new ResponseEntity<>(new TokenDto(jwt), httpHeaders, HttpStatus.CREATED);
    }


    @Operation(summary = "카카오 로그인",
            description = "카카오 api 를 활용해서 로그인 합니다. 프론트에서 받은 code 를 활용해서 jwt 토큰을 발급합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "성공적으로 토큰 발급"),
            @ApiResponse(responseCode = "500", description = "카카오 인증 과정중 오류")
    })
    @GetMapping("kakao")
    public ResponseEntity<TokenDto> kakaoCallBack(@RequestParam String code) {
        String accessToken = oauthService.getKakaoAccessToken(code);
        KakaoInfo kakaoInfo = oauthService.getUserKakaoInfo(accessToken);

        String jwt = oauthService.createToken(kakaoInfo);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtAuthenticationFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);

        return ResponseEntity.status(HttpStatus.CREATED).headers(httpHeaders).body(new TokenDto(jwt));
    }
}
