package com.option.refit.global.error.calendarError;

import com.option.refit.global.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CalendarErrorCode implements ErrorCode {
    CALENDAR_MEMO_NOT_FOUND(HttpStatus.NOT_FOUND,"존재하지 않는 메모입니다"),
    CALENDAR_MEMO_ACCESS_DENIED(HttpStatus.FORBIDDEN,"본인이 작성한 메모만 수정/삭제할 수 있습니다"),
    INVALID_DATE_FORMAT(HttpStatus.BAD_REQUEST,"날짜 형식이 올바르지 않습니다");

    private final HttpStatus httpStatus;
    private final String message;
}
