package com.project.durumoongsil.teutoo.security.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

@Slf4j
public class SecurityUtil {
    private SecurityUtil() {}

    public static Optional<String> getCurrentLoginId() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = null;
        if (authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
            email = springSecurityUser.getUsername();
        }  else if (authentication.getPrincipal() instanceof String) {
            email = (String) authentication.getPrincipal();
        }
        return Optional.ofNullable(email);
    }
}
