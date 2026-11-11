package com.option.refit.global.error.attendanceError;

import com.option.refit.global.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AttendanceErrorCode implements ErrorCode {
    NFC_TAG_NOT_FOUND(HttpStatus.NOT_FOUND, "등록되지 않은 NFC 태그입니다"),
    NFC_TAG_ALREADY_LINKED(HttpStatus.CONFLICT, "이미 수업에 연결된 NFC 태그입니다. 기존 연결을 먼저 해제해주세요"),
    NFC_CLASS_ALREADY_HAS_TAG(HttpStatus.CONFLICT, "이미 NFC 태그가 연결된 수업입니다. 기존 연결을 먼저 해제해주세요"),
    NFC_TAG_IN_USE(HttpStatus.CONFLICT, "수업에 연결된 NFC 태그는 삭제할 수 없습니다. 먼저 연결을 해제해주세요"),
    NFC_TAG_INACTIVE(HttpStatus.BAD_REQUEST, "사용할 수 없는 NFC 태그입니다"),
    NFC_TAG_NOT_LINKED(HttpStatus.BAD_REQUEST, "수업에 연결되지 않은 NFC 태그입니다"),
    NFC_TAG_SCHEDULE_MISMATCH(HttpStatus.BAD_REQUEST, "태그에 연결된 수업의 회차가 아닙니다"),
    OUTSIDE_ATTENDANCE_TIME(HttpStatus.BAD_REQUEST, "출석 가능한 시간이 아닙니다"),
    ALREADY_ATTENDED(HttpStatus.CONFLICT, "이미 출석 처리된 수업입니다"),
    ABSENCE_ALREADY_RECORDED(HttpStatus.CONFLICT, "선생님이 결석으로 처리한 회차입니다. 선생님에게 문의해주세요");

    private final HttpStatus httpStatus;
    private final String message;
}
