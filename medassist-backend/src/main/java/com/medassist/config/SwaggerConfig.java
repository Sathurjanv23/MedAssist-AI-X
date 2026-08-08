package com.medassist.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * SpringDoc OpenAPI 3.0 Swagger configuration.
 */
@Configuration
public class SwaggerConfig {

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @Bean
    public OpenAPI medAssistOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(servers())
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", bearerScheme()));
    }

    private Info apiInfo() {
        return new Info()
                .title("MedAssist AI X API")
                .description("Your Personal AI Healthcare Operating System — REST API Documentation\n\n" +
                             "**Authentication:** Use the /api/auth/login endpoint to get a JWT token, " +
                             "then click 'Authorize' and enter: `Bearer <your-token>`")
                .version("1.0.0")
                .contact(new Contact()
                        .name("MedAssist AI X Team")
                        .email("api@medassist.ai")
                        .url("https://medassist.ai"))
                .license(new License()
                        .name("Proprietary")
                        .url("https://medassist.ai/terms"));
    }

    private List<Server> servers() {
        Server devServer = new Server()
                .url("http://localhost:8080")
                .description("Development Server");
        Server prodServer = new Server()
                .url("https://api.medassist.ai")
                .description("Production Server");
        return "prod".equals(activeProfile)
                ? List.of(prodServer)
                : List.of(devServer, prodServer);
    }

    private SecurityScheme bearerScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization")
                .description("JWT Bearer token. Format: Bearer {token}");
    }
}
