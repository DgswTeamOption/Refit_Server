package com.option.refit.global.error.makeupError;

import com.option.refit.global.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MakeupErrorCode implements ErrorCode {
    MAKEUP_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 보강 신청입니다"),
    MAKEUP_CAPACITY_EXCEEDED(HttpStatus.CONFLICT, "정원이 마감된 보강 일정입니다"),
    ALREADY_PROCESSED_MAKEUP(HttpStatus.CONFLICT, "이미 처리된 보강 신청입니다"),
    MAKEUP_ACCESS_DENIED(HttpStatus.FORBIDDEN, "본인 신청만 취소/수정할 수 있습니다"),
    INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "지난 날짜로는 신청할 수 없습니다"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "선생님만 이용할 수 있는 기능입니다"),
    METHOD_ARGUMENT_NOT_VALID(HttpStatus.BAD_REQUEST,"필수 입력값을 확인해주세요");

    private final HttpStatus httpStatus;
    private final String message;
}