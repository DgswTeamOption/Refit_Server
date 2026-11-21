package com.option.refit.global.error.scheduleError;

import com.option.refit.global.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ScheduleErrorCode implements ErrorCode {
    SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND,"존재하지 않는 일정입니다"),
    INVALID_DATE_FORMAT(HttpStatus.BAD_REQUEST,"날짜 형식이 올바르지 않습니다"),
    STUDENT_NOT_IN_CLASS(HttpStatus.BAD_REQUEST,"해당 수업에 참여 중인 학생이 아닙니다"),
    SCHEDULE_RULE_NOT_FOUND(HttpStatus.NOT_FOUND,"존재하지 않는 반복 일정입니다"),
    SCHEDULE_OVERLAP(HttpStatus.CONFLICT,"같은 요일에 시간이 겹치는 수업이 이미 있습니다");

    private final HttpStatus httpStatus;
    private final String message;
}
