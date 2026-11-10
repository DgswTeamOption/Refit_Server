package com.option.refit.global.error.absenceError;

import com.option.refit.global.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AbsenceErrorCode implements ErrorCode {
    INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "지난 날짜로는 신청할 수 없습니다"),
    ABSENCE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 결석 신청입니다"),
    ABSENCE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "해당 결석 신청에 대한 권한이 없습니다"),
    ALREADY_PROCESSED_ABSENCE(HttpStatus.CONFLICT, "이미 처리된 결석 신청입니다"),
    DUPLICATE_ABSENCE(HttpStatus.CONFLICT, "이미 해당 날짜에 신청한 결석입니다"),
    METHOD_ARGUMENT_NOT_VALID(HttpStatus.BAD_REQUEST,"필수 입력값을 확인해주세요");

    private final HttpStatus httpStatus;
    private final String message;
}