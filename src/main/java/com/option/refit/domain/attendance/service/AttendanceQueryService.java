package com.option.refit.domain.attendance.service;

import com.option.refit.domain.attendance.dto.AttendanceResponse;
import com.option.refit.domain.attendance.dto.AttendanceStatusResponse;
import com.option.refit.domain.attendance.dto.AttendanceSummaryResponse;
import com.option.refit.domain.attendance.dto.AttendanceUncheckedResponse;
import com.option.refit.global.jwt.JwtPrincipal;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceQueryService {

    // 학생: 본인의 월별 출석 내역
    List<AttendanceResponse> getMyAttendances(JwtPrincipal principal, Long classId, int year, int month);

    // 선생님: 담당 수업의 날짜별 학생 출석 현황
    List<AttendanceStatusResponse> getClassAttendances(JwtPrincipal principal, Long classId, LocalDate date);

    // 선생님: 현재 수강생 명단과 출석 기록을 비교해, 그 날짜에 출석·지각 기록이 없는 학생 목록
    List<AttendanceUncheckedResponse> getUncheckedStudents(JwtPrincipal principal, Long classId, LocalDate date);

    // 월별 출석·결석 집계 (선생님: 수업 전체 학생, 학생: 본인만)
    List<AttendanceSummaryResponse> getMonthlySummary(JwtPrincipal principal, Long classId, int year, int month);
}
