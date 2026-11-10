package com.option.refit.global.error.attendanceError;

import com.option.refit.global.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AttendanceErrorCode implements ErrorCode {
    NFC_TAG_NOT_FOUND(HttpStatus.NOT_FOUND, "등록되지 않은 NFC 태그입니다"),
    DUPLICATE_NFC_TAG(HttpStatus.CONFLICT, "이미 등록된 NFC 태그입니다"),
    NFC_TAG_CLASS_MISMATCH(HttpStatus.BAD_REQUEST, "해당 수업의 NFC 태그가 아닙니다"),
    OUTSIDE_ATTENDANCE_TIME(HttpStatus.BAD_REQUEST, "출석 가능한 시간이 아닙니다"),
    ALREADY_ATTENDED(HttpStatus.CONFLICT, "이미 출석 처리된 수업입니다");

    private final HttpStatus httpStatus;
    private final String message;
}
