package com.zhice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 3 (Swagger) 配置类
 * 包含基础信息配置以及全局 JWT 鉴权配置
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI zhiCeOpenAPI() {
        // 定义安全协议的名称，这里取名为 "BearerAuth"
        final String securitySchemeName = "BearerAuth";

        return new OpenAPI()
                // 1. 基础 API 信息描述
                .info(new Info()
                        .title("智策 - 团队文档协作平台 API")
                        .description("基于 Spring Boot 3 构建的大学生竞赛管理与AI辅助协作平台接口文档")
                        .version("v1.0.0")
                        .contact(new Contact().name("智策开发团队")))

                // 2. 配置安全方案 (Components)
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP) // 类型为 HTTP
                                .scheme("bearer")               // 协议为 bearer
                                .bearerFormat("JWT")            // 格式为 JWT
                                .in(SecurityScheme.In.HEADER)   // Token 放在请求头中
                                .description("请在下方输入调用 /api/v1/auth/login 接口获取的 JWT Token。注意：Swagger 会自动在前面加上 'Bearer '，所以你只需要直接粘贴 Token 字符串即可。")))

                // 3. 全局应用安全需求 (SecurityRequirement)，让所有接口默认都需要这个安全方案
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName));
    }
}