package com.option.refit.global.error.classError;

import com.option.refit.global.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ClazzErrorCode implements ErrorCode {
    CLASS_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 수업입니다"),
    INVALID_CLASS_CODE(HttpStatus.BAD_REQUEST,"참여 코드가 올바르지 않거나 만료되었습니다"),
    ALREADY_JOINED_CLASS(HttpStatus.CONFLICT,"이미 참여 중인 수업입니다"),
    CLASS_ACCESS_DENIED(HttpStatus.FORBIDDEN,"해당 수업에 접근할 수 없습니다"),
    CLASS_PERMISSION_DENIED(HttpStatus.FORBIDDEN,"선생님만 이용할 수 있는 기능입니다"),
    METHOD_ARGUMENT_NOT_VALID(HttpStatus.BAD_REQUEST,"필수 입력값을 확인해주세요");

    private final HttpStatus httpStatus;
    private final String message;
}