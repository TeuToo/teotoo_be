package com.project.durumoongsil.teutoo.chat.config;

import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.stereotype.Component;

@Component
public class WebsocketConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {


    // csrf 토큰 비활성화
    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }

}
