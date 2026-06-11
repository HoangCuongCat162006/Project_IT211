package com.example.project_it211.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("IT211 - Hệ thống Quản lý Khóa học & Chấm điểm Đồ án")
                        .description("RESTful API cho hệ thống quản lý khóa học và chấm điểm đồ án. "
                                + "Hệ thống hỗ trợ 3 vai trò: ADMIN, LECTURER, STUDENT.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("IT211 Project Team")
                                .email("it211@example.com")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Nhập JWT token vào đây (không cần prefix 'Bearer ')")));
    }
}
