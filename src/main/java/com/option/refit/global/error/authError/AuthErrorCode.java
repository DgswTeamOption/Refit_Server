package com.option.refit.global.error.authError;

import com.option.refit.global.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 일치하지 않습니다"),
    DUPLICATE_USERNAME(HttpStatus.CONFLICT,"이미 사용 중인 아이디입니다"),
    INACTIVE_USER(HttpStatus.FORBIDDEN,"탈퇴하거나 정지된 계정입니다");

    private final HttpStatus httpStatus;
    private final String message;
}