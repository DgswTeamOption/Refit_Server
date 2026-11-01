package com.option.refit.global.error.jwtError;

import com.option.refit.global.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum JwtErrorCode implements ErrorCode {
    MISSING_TOKEN(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다"),
    EXPIRED_TOKEN(HttpStatus.BAD_REQUEST,"로그인이 만료되었습니다. 다시 로그인해주세요"),
    INVALID_TOKEN_SIGNATURE(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다"),
    MALFORMED_TOKEN(HttpStatus.UNAUTHORIZED, "토큰 형식이 올바르지 않습니다");

    private final HttpStatus httpStatus;
    private final String message;
}