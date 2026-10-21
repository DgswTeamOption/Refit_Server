package com.option.refit.global.error.userError;

import com.option.refit.global.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다"),
    USER_ACCESS_DENIED(HttpStatus.FORBIDDEN, "본인의 정보만 조회할 수 있습니다"),
    DUPLICATE_USERNAME_ON_UPDATE(HttpStatus.CONFLICT,"이미 사용 중인 아이디입니다"),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST,"현재 비밀번호가 일치하지 않습니다"),
    INVALID_FILE_FORMAT(HttpStatus.BAD_REQUEST,"지원하지 않는 이미지 형식입니다"),
    FILE_SIZE_EXCEEDED(HttpStatus.PAYLOAD_TOO_LARGE,"이미지 용량이 너무 큽니다"),
    METHOD_ARGUMENT_NOT_VALID(HttpStatus.BAD_REQUEST,"입력값을 다시 확인해주세요");

    private final HttpStatus httpStatus;
    private final String message;
}