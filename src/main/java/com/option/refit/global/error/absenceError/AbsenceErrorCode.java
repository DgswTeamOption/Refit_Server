package com.option.refit.global.error.absenceError;

import com.option.refit.global.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AbsenceErrorCode implements ErrorCode {
    INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "지난 날짜로는 신청할 수 없습니다"),
    METHOD_ARGUMENT_NOT_VALID(HttpStatus.BAD_REQUEST,"필수 입력값을 확인해주세요");

    private final HttpStatus httpStatus;
    private final String message;
}