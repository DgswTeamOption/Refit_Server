package com.option.refit.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor // 필수 인자를 가진 생성자를 자동으로 생성
public class ErrorResponse {
    private LocalDateTime timeStamp;
    private int status;
    private String message;

    public static ErrorResponse from(CustomException e) {
        return new ErrorResponse(
                LocalDateTime.now(),
                e.getStatus().value(),
                e.getMessage()
        );
    }

    public static ErrorResponse of(HttpStatus status, String message) {
        return new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                message
        );
    }

    // ApiResponse 에서 사용하기 위한 코드
    public static ErrorResponse of(ErrorCode errorCode) {
        return of(errorCode.getHttpStatus(), errorCode.getMessage());
    }
}
