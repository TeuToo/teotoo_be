package com.project.durumoongsil.teutoo.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        //필요한 권한이 없이 접근하려 할때 403
        log.error("Denied error: {}", accessDeniedException.getMessage());

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        Map<String, Object> errorInfo = new HashMap<>();
        errorInfo.put("code", HttpServletResponse.SC_FORBIDDEN);
        errorInfo.put("message", "권한이 없습니다.");

        String jsonError = new ObjectMapper().writeValueAsString(errorInfo);

        response.getWriter().write(jsonError);
    }
}
