package com.option.refit.domain.attendance.controller;

import com.option.refit.domain.attendance.dto.AttendanceMarkRequest;
import com.option.refit.domain.attendance.dto.AttendanceResponse;
import com.option.refit.domain.attendance.dto.AttendanceStatusResponse;
import com.option.refit.domain.attendance.dto.AttendanceSummaryResponse;
import com.option.refit.domain.attendance.dto.AttendanceUncheckedResponse;
import com.option.refit.domain.attendance.service.AttendanceMarkService;
import com.option.refit.domain.attendance.service.AttendanceQueryService;
import com.option.refit.global.common.ApiResponse;
import com.option.refit.global.jwt.JwtPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/class/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceMarkService attendanceMarkService;
    private final AttendanceQueryService attendanceQueryService;

    // 출결 상태 변경 (담당 선생님) - 명세: PATCH api/class/attendance/{classId}/{attendanceId}
    // 기록이 아직 없는 일정도 처리해야 하므로 경로 값은 attendanceId가 아닌 scheduleId를 사용
    // 200 성공 / 400 status 누락·오류 / 403 선생님 아님·담당 수업 아님 / 404 없는 수업·일정
    @PatchMapping("/{classId}/{scheduleId}")
    public ApiResponse<AttendanceResponse> markAttendance(
            @AuthenticationPrincipal JwtPrincipal principal,
            @PathVariable Long classId,
            @PathVariable Long scheduleId,
            @Valid @RequestBody AttendanceMarkRequest request) {
        return ApiResponse.ok(
                attendanceMarkService.markAttendance(principal, classId, scheduleId, request.getStatus()));
    }

    // 내 월별 출석 내역 (학생)
    // 200 성공 / 400 잘못된 year·month / 403 학생 아님·수업 미참여 / 404 없는 수업
    @GetMapping("/{classId}/me")
    public ApiResponse<List<AttendanceResponse>> getMyAttendances(
            @AuthenticationPrincipal JwtPrincipal principal,
            @PathVariable Long classId,
            @RequestParam int year,
            @RequestParam int month) {
        return ApiResponse.ok(attendanceQueryService.getMyAttendances(principal, classId, year, month));
    }

    // 날짜별 학생 출석 현황 (담당 선생님) - date 형식 yyyy-MM-dd
    // 200 성공 / 400 잘못된 날짜 형식 / 403 선생님 아님·담당 수업 아님 / 404 없는 수업
    @GetMapping("/{classId}")
    public ApiResponse<List<AttendanceStatusResponse>> getClassAttendances(
            @AuthenticationPrincipal JwtPrincipal principal,
            @PathVariable Long classId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ApiResponse.ok(attendanceQueryService.getClassAttendances(principal, classId, date));
    }

    // 미출석 학생 (담당 선생님) - 수강생 명단(left_at IS NULL)에서 그 날짜에 출석·지각 기록이 없는 학생. date 형식 yyyy-MM-dd
    // 200 성공 / 400 잘못된 날짜 형식 / 403 선생님 아님·담당 수업 아님 / 404 없는 수업
    @GetMapping("/{classId}/unchecked")
    public ApiResponse<List<AttendanceUncheckedResponse>> getUncheckedStudents(
            @AuthenticationPrincipal JwtPrincipal principal,
            @PathVariable Long classId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ApiResponse.ok(attendanceQueryService.getUncheckedStudents(principal, classId, date));
    }

    // 월별 출결 통계 - 선생님은 수업 전체 학생, 학생은 본인만
    // 200 성공 / 400 잘못된 year·month / 403 담당 수업 아님·수업 미참여 / 404 없는 수업
    @GetMapping("/stats/{classId}")
    public ApiResponse<List<AttendanceSummaryResponse>> getMonthlySummary(
            @AuthenticationPrincipal JwtPrincipal principal,
            @PathVariable Long classId,
            @RequestParam int year,
            @RequestParam int month) {
        return ApiResponse.ok(attendanceQueryService.getMonthlySummary(principal, classId, year, month));
    }
}
