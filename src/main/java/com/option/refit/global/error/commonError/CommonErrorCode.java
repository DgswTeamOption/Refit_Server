package com.option.refit.global.error.commonError;

import com.option.refit.global.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {
    METHOD_ARGUMENT_NOT_VALID(HttpStatus.NOT_FOUND, "입력값을 다시 확인해주세요"),
    HTTP_MESSAGE_NOT_READABLE(HttpStatus.BAD_REQUEST,"요청 형식이 올바르지 않습니다"),
    NO_HANDLER_FOUND(HttpStatus.CONFLICT,"존재하지 않는 요청입니다"),
    DATA_INTEGRITY_VIOLATION(HttpStatus.FORBIDDEN,"이미 등록된 정보이거나 처리할 수 없는 요청입니다"),
    SERVER_ERROR(HttpStatus.FORBIDDEN,"일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요");

    private final HttpStatus httpStatus;
    private final String message;
}