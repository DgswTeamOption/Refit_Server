package com.option.refit.domain.attendance.service;

import com.option.refit.domain.attendance.dto.AttendanceResponse;
import com.option.refit.global.jwt.JwtPrincipal;

public interface NfcAttendanceService {

    // 학생이 NFC 태그로 출석 처리
    AttendanceResponse checkIn(JwtPrincipal principal, Long classId, String tagUid);
}
