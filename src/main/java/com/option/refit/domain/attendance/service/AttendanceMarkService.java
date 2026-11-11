package com.option.refit.domain.attendance.service;

import com.option.refit.domain.attendance.dto.AttendanceResponse;
import com.option.refit.domain.attendance.entity.AttendanceStatus;
import com.option.refit.global.jwt.JwtPrincipal;

public interface AttendanceMarkService {

    // 선생님이 일정 단위로 출석·결석 처리 (이미 기록이 있으면 상태 변경)
    AttendanceResponse markAttendance(JwtPrincipal principal, Long classId, Long scheduleId, AttendanceStatus status);
}
