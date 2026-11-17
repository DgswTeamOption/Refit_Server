package com.option.refit.global.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor // 필수 인자를 가진 생성자를 자동으로 생성
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;

    @Schema(description = "에러 코드 (프론트 분기용)", example = "CLASS_NOT_FOUND")
    private String code;

    private String message;

    @Schema(description = "검증 실패 시 필드별 사유. 검증 실패가 아니면 응답에 포함되지 않음")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<FieldError> errors;

    public record FieldError(String field, String reason) {
    }

    public static ErrorResponse from(CustomException e) {
        return of(e.getErrorCode(), e.getMessage());
    }

    // 메시지를 따로 지정해야 하는 경우 (코드/상태는 errorCode 기준)
    public static ErrorResponse of(ErrorCode errorCode, String message) {
        return new ErrorResponse(
                LocalDateTime.now(),
                errorCode.getHttpStatus().value(),
                errorCode.name(),
                message,
                null
        );
    }

    // 검증 실패: 필드별 사유 포함
    public static ErrorResponse of(ErrorCode errorCode, String message, List<FieldError> errors) {
        return new ErrorResponse(
                LocalDateTime.now(),
                errorCode.getHttpStatus().value(),
                errorCode.name(),
                message,
                errors
        );
    }

    // ApiResponse 에서 사용하기 위한 코드
    public static ErrorResponse of(ErrorCode errorCode) {
        return of(errorCode, errorCode.getMessage());
    }
}
