package com.project.durumoongsil.teutoo.security.service;

import com.project.durumoongsil.teutoo.security.oauth2.CustomOauth2User;
import com.project.durumoongsil.teutoo.security.jwt.TokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class Oauth2SuccessHandler implements AuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (authentication instanceof OAuth2AuthenticationToken authToken) {
            CustomOauth2User customUser = (CustomOauth2User) authToken.getPrincipal();
            String email = customUser.getEmail();

            String jwt = tokenProvider.createToken(authToken, email); // 토큰 생성

            // 쿠키 생성
            Cookie jwtCookie = new Cookie("token", jwt);
            jwtCookie.setHttpOnly(false); // JavaScript를 통한 접근 방지 : true , 접근허용 : false
            jwtCookie.setPath("/"); // 전체 도메인에 대해 쿠키 접근 허용
            jwtCookie.setSecure(false); // 쿠키 보안을 위해 HTTPS 환경 : true HTTP : false
            jwtCookie.setMaxAge(60 * 60);

            // 쿠키를 응답에 추가
            response.addCookie(jwtCookie);
        } else {
            // 적절한 오류 처리
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "인증 정보가 없습니다.");
        }
    }
}
