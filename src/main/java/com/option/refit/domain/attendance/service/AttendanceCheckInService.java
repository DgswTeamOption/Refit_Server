package com.option.refit.domain.attendance.service;

import com.option.refit.domain.attendance.dto.AttendanceResponse;
import com.option.refit.global.jwt.JwtPrincipal;

import java.time.LocalDateTime;

public interface AttendanceCheckInService {

    // 학생이 NFC로 수업 회차에 출석. classId는 태그에 연결된 수업, now는 서버 시간(출석 시각으로 기록)
    AttendanceResponse checkInByNfc(JwtPrincipal principal, Long classId, Long scheduleId, LocalDateTime now);
}
