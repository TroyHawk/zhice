package com.zhice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 3 (Swagger) 配置类
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI zhiCeOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("智策 - 团队文档协作平台 API")
                        .description("基于 Spring Boot 3 构建的大学生竞赛管理与AI辅助协作平台接口文档")
                        .version("v1.0.0")
                        .contact(new Contact().name("智策开发团队")));
    }
}