package com.option.refit.global.jwt;

import com.option.refit.global.error.ErrorCode;
import com.option.refit.global.error.ErrorResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

// 필터/핸들러는 @RestControllerAdvice를 거치지 않으므로, 기존 ErrorResponse 형식 그대로 응답을 직접 씀
@Component
@RequiredArgsConstructor
public class JwtErrorResponseWriter {

    private final ObjectMapper objectMapper;

    public void write(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), ErrorResponse.of(errorCode));
    }
}
