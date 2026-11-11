package com.option.refit.domain.attendance.entity;

// 출석 처리 상태 (문자열로 저장되므로 기존 값 이름 변경 금지)
public enum AttendanceStatus {
    PRESENT,
    ABSENT,
    LATE // NFC 출석에서 정상 출석 시간이 지난 뒤 출석한 경우
}
