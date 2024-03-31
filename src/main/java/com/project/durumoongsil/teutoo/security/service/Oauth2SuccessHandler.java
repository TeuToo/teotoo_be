package com.project.durumoongsil.teutoo.security.service;

import com.project.durumoongsil.teutoo.security.oauth2.CustomOauth2User;
import com.project.durumoongsil.teutoo.security.jwt.TokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

            // 카카오에 저장된 이메일을 사용하여 토큰 생성
            String token = tokenProvider.createToken(authToken, email);

            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpStatus.OK.value());
            response.getWriter().write("{\"access_token\": \"" + token + "\"}");
        }
    }
}
