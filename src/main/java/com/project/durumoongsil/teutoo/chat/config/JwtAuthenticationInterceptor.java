package com.project.durumoongsil.teutoo.chat.config;

import com.project.durumoongsil.teutoo.exception.UnauthorizedActionException;
import com.project.durumoongsil.teutoo.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.logging.Logger;


@Component
@RequiredArgsConstructor
public class JwtAuthenticationInterceptor implements ChannelInterceptor {

    private final TokenProvider tokenProvider;
    public static final String AUTHORIZATION_HEADER = "Authorization";

    private final Logger myLogger = Logger.getLogger(this.getClass().getName());

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        StompCommand command = accessor.getCommand();

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader(AUTHORIZATION_HEADER);
            if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
                String authToken = token.substring(7);

                if (StringUtils.hasText(authToken) && tokenProvider.validateToken(authToken)) {
                    Authentication authentication = tokenProvider.getAuthentication(authToken);

                    myLogger.info("name is " + authentication.getName() + " auth? : " + authentication.isAuthenticated());

                    // 시큐리티에서 참조하기 위함,
                    accessor.setUser(authentication);
                }
            }


        }
        return message;
    }
}