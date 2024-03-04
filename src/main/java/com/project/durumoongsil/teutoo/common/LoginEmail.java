package com.project.durumoongsil.teutoo.common;

import com.project.durumoongsil.teutoo.exception.UserUnauthorizedException;
import com.project.durumoongsil.teutoo.security.util.SecurityUtil;

public class LoginEmail {
    public static String getLoginUserEmail() {
        return SecurityUtil.getCurrentLoginId().orElseThrow(() ->
                new UserUnauthorizedException("인증 권한이 없습니다."));
    }
}
