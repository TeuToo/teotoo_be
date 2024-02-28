package com.project.durumoongsil.teutoo.security.service;

import com.project.durumoongsil.teutoo.exception.UserUnauthorizedException;
import com.project.durumoongsil.teutoo.security.util.SecurityUtil;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

    /**
     * 스프링 시큐리티 + jwt 토큰을 통해서 현재 인증된 사용자의 아이디(email)를 가져온다.
     */
    public String getLoginedUserEmail() {
        return SecurityUtil.getCurrentLoginId().orElseThrow(() ->
                new UserUnauthorizedException("인증 권한이 없습니다."));
    }
}
