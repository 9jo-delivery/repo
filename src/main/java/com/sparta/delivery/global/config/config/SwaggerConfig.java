package com.sparta.delivery.global.config.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
	info = @Info(title = "9조 API", version = "v1", description = "API 명세서"),
	security = @SecurityRequirement(name = "bearerAuth") // 모든 API 요청에 아래 설정한 인증 적용
)
@SecurityScheme(
	name = "bearerAuth",
	type = SecuritySchemeType.HTTP,
	bearerFormat = "JWT",
	scheme = "bearer" // Bearer 토큰 방식 사용
)
public class SwaggerConfig {
}
