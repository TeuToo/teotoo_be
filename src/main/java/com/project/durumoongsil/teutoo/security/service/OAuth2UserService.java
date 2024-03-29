package com.project.durumoongsil.teutoo.security.service;

import com.project.durumoongsil.teutoo.login.CustomOauth2User;
import com.project.durumoongsil.teutoo.login.dto.KakaoOAuth2Response;
import com.project.durumoongsil.teutoo.member.domain.Member;
import com.project.durumoongsil.teutoo.member.domain.Role;
import com.project.durumoongsil.teutoo.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        KakaoOAuth2Response kakaoOAuth2Response = KakaoOAuth2Response.from(oAuth2User.getAttributes());
        Optional<Member> memberByEmail = memberRepository.findMemberByEmail(kakaoOAuth2Response.email());
        Member member;
        if (memberByEmail.isPresent()) {
            member = memberByEmail.get();
        } else {
            // 가입 이력이 없다면 여기서 카카오 회원 생성
            member = createOauth2Member(kakaoOAuth2Response);
            memberRepository.save(member);
        }
        return new CustomOauth2User(oAuth2User, member.getEmail());
    }

    private Member createOauth2Member(KakaoOAuth2Response kakaoOAuth2Response) {
        return Member.builder()
                .email(kakaoOAuth2Response.email())
                .role(Role.USER) // 우선 조회는 되야하니 기본값으로 USER 권한 부여
                .build();
    }
}
