package com.project.durumoongsil.teutoo.login.service;

import com.google.gson.Gson;
import com.project.durumoongsil.teutoo.exception.KakaoAccessTokenException;
import com.project.durumoongsil.teutoo.exception.KakaoUserInfoException;
import com.project.durumoongsil.teutoo.login.dto.KakaoInfo;
import com.project.durumoongsil.teutoo.login.dto.Oauth2Response;
import com.project.durumoongsil.teutoo.member.domain.Member;
import com.project.durumoongsil.teutoo.member.domain.Role;
import com.project.durumoongsil.teutoo.member.repository.MemberRepository;
import com.project.durumoongsil.teutoo.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class OauthService {

    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final RestTemplate restTemplate;
    private final Gson gson;

    @Value("${oauth2.kakao.token-uri}")
    private String tokenUri;

    @Value("${oauth2.kakao.redirect-uri}")
    private String redirectUri;

    @Value("${oauth2.kakao.user-info-uri}")
    private String userInfoUri;

    @Value("${oauth2.kakao.client-id}")
    private String clientId;



    public String getKakaoAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "authorization_code");
        map.add("client_id", clientId);
        map.add("redirect_uri", redirectUri);
        map.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        String responseBody = "";
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(tokenUri, request, String.class);
            responseBody = response.getBody();
        } catch (RestClientException e) {
            throw new KakaoAccessTokenException("Failed to retrieve Kakao access token");
        }

        Oauth2Response oauth2Response = gson.fromJson(responseBody, Oauth2Response.class);

        return oauth2Response.getAccess_token();
    }

    public KakaoInfo getUserKakaoInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(userInfoUri, HttpMethod.GET, entity, String.class);
            return gson.fromJson(response.getBody(), KakaoInfo.class);
        } catch (Exception e) {
            throw new KakaoUserInfoException("Failed to retrieve user information from Kakao");
        }
    }


    public String createToken(Authentication authentication) {
        return tokenProvider.createToken(authentication);
    }


    public String createToken(KakaoInfo kakaoInfo) {
        String email = kakaoInfo.getKakao_account().getEmail();
        Member member = memberRepository.findMemberByEmail(email).orElseGet(()->createOauth2Member(kakaoInfo));
        return tokenProvider.createToken(email, String.valueOf(member.getRole()));
    }

    private Member createOauth2Member(KakaoInfo kakaoInfo) {
        Member member = Member.builder()
                .email(kakaoInfo.getKakao_account().getEmail())
                .name(kakaoInfo.getProperties().getNickname())
                .role(Role.USER) // 우선 조회는 되야하니 기본값으로 USER 권한 부여
                .build();
        return memberRepository.save(member);
    }
}
