package com.project.durumoongsil.teutoo.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        log.error("Unauthorized error: {}", authException.getMessage());

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        Map<String, Object> errorInfo = new HashMap<>();
        errorInfo.put("code", HttpServletResponse.SC_UNAUTHORIZED);
        errorInfo.put("message", "일치하는 회원이 없습니다.");

        String jsonError = new ObjectMapper().writeValueAsString(errorInfo);

        response.getWriter().write(jsonError);
    }
}
