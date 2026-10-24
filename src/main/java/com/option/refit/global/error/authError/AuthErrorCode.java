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
    INACTIVE_USER(HttpStatus.FORBIDDEN,"탈퇴하거나 정지된 계정입니다"),
    EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "인증 메일 발송에 실패했습니다"),
    INVALID_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, "인증 코드가 일치하지 않습니다"),
    VERIFICATION_CODE_EXPIRED(HttpStatus.BAD_REQUEST, "인증 코드가 만료되었거나 요청 내역이 없습니다"),
    TOO_MANY_VERIFICATION_ATTEMPTS(HttpStatus.TOO_MANY_REQUESTS, "인증 시도 횟수를 초과했습니다. 코드를 다시 요청해주세요");

    private final HttpStatus httpStatus;
    private final String message;
}