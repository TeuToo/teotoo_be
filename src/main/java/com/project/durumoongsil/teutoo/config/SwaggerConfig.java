package com.project.durumoongsil.teutoo.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    private static final String API_NAME = "TEUTOO API";
    private static final String API_VERSION = "1.0.0";
    private static final String API_DESCRIPTION = "description";
    private static final String SERVER_URL = "https://api.teutoo.site";


    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title(API_NAME)
                .version(API_VERSION)
                .description(API_DESCRIPTION);

        final String securitySchemeName = "jwt Auth";

        // API 요청헤더에 인증정보 포함
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList(securitySchemeName);
        // SecuritySchemes 등록
        Components components = new Components()
                .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                        .name(securitySchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")); // 임의로 JWT 토큰 지정

        Server server = new Server();
        server.setUrl(SERVER_URL);
        List<Server> servers = List.of(server);

        return new OpenAPI()
                .info(info)
                .addSecurityItem(securityRequirement)
                .components(components)
                .servers(servers);
    }


}
