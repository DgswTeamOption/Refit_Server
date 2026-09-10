package com.option.refit.global.error.securityError;

import com.option.refit.global.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SecurityErrorCode implements ErrorCode {
    ROLE_ACCESS_DENIED(HttpStatus.FORBIDDEN,"접근 권한이 없습니다");

    private final HttpStatus httpStatus;
    private final String message;
}