package com.option.refit.global.config;

import com.option.refit.global.error.ErrorResponse;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String BEARER_AUTH = "bearerAuth";

    @Bean
    public OpenAPI openAPI() {
        SecurityScheme bearerScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");

        // ErrorResponse(+FieldError) 스키마를 components에 등록해 공통 에러 응답에서 참조
        Components components = new Components().addSecuritySchemes(BEARER_AUTH, bearerScheme);
        ModelConverters.getInstance().readAll(ErrorResponse.class).forEach(components::addSchemas);

        return new OpenAPI()
                .info(new Info()
                        .title("Refit API")
                        .description("Refit 백엔드 API 문서")
                        .version("v1.0.0"))
                .components(components)
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH));
    }

    @Bean
    public GroupedOpenApi allApi() {
        return GroupedOpenApi.builder()
                .group("refit")
                .pathsToMatch("/**")
                .build();
    }

    // 전 operation에 공통 에러 응답(400/401/403/404/409/500)을 일괄 부착. 이미 정의된 응답은 덮어쓰지 않음
    @Bean
    public OperationCustomizer commonErrorResponses() {
        return (operation, handlerMethod) -> {
            ApiResponses responses = operation.getResponses();
            if (responses == null) {
                responses = new ApiResponses();
                operation.setResponses(responses);
            }
            for (Object[] error : COMMON_ERRORS) {
                responses.computeIfAbsent((String) error[0], code -> errorResponse((String) error[1]));
            }
            return operation;
        };
    }

    private static final Object[][] COMMON_ERRORS = {
            {"400", "잘못된 요청 (입력값 검증 실패 등)"},
            {"401", "인증 실패 (토큰 없음/만료/유효하지 않음)"},
            {"403", "권한 없음"},
            {"404", "대상을 찾을 수 없음"},
            {"409", "충돌 (중복 등록 등)"},
            {"500", "서버 오류"}
    };

    private static ApiResponse errorResponse(String description) {
        Schema<?> schema = new Schema<>().$ref("#/components/schemas/ErrorResponse");
        return new ApiResponse()
                .description(description)
                .content(new Content().addMediaType("application/json", new MediaType().schema(schema)));
    }
}
