package com.option.refit.domain.schedule.controller;

import com.option.refit.domain.schedule.dto.ScheduleRequest;
import com.option.refit.domain.schedule.dto.ScheduleResponse;
import com.option.refit.domain.schedule.service.ScheduleCreateService;
import com.option.refit.domain.schedule.service.ScheduleDeleteService;
import com.option.refit.domain.schedule.service.ScheduleGetService;
import com.option.refit.domain.schedule.service.ScheduleUpdateService;
import com.option.refit.domain.user.entity.Role;
import com.option.refit.global.common.ApiResponse;
import com.option.refit.global.error.securityError.exception.RoleAccessDeniedException;
import com.option.refit.global.jwt.JwtPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/class")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleGetService scheduleGetService;
    private final ScheduleCreateService scheduleCreateService;
    private final ScheduleUpdateService scheduleUpdateService;
    private final ScheduleDeleteService scheduleDeleteService;

    // 수업 일정 조회 (로그인)
    @GetMapping("/schedule/{classId}")
    public ApiResponse<List<ScheduleResponse>> getSchedules(@PathVariable Long classId) {
        return ApiResponse.ok(scheduleGetService.getSchedulesByClass(classId));
    }

    // 캘린더 수업일 표시 (로그인)
    @GetMapping("/calendar/{classId}")
    public ApiResponse<List<ScheduleResponse>> getMonthlyCalendar(
            @PathVariable Long classId,
            @RequestParam int year,
            @RequestParam int month) {
        return ApiResponse.ok(scheduleGetService.getMonthlyCalendar(classId, year, month));
    }

    // 일정 등록 (선생님)
    @PostMapping("/schedule/{classId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Long> createSchedule(
            @AuthenticationPrincipal JwtPrincipal principal,
            @PathVariable Long classId,
            @Valid @RequestBody ScheduleRequest.CreateOrUpdate request) {
        requireTeacher(principal);
        return ApiResponse.ok(scheduleCreateService.createSchedule(classId, request));
    }

    // 일정 수정 (선생님)
    @PatchMapping("/schedule/{classId}/{scheduleId}")
    public ApiResponse<Void> updateSchedule(
            @AuthenticationPrincipal JwtPrincipal principal,
            @PathVariable Long classId,
            @PathVariable Long scheduleId,
            @Valid @RequestBody ScheduleRequest.CreateOrUpdate request) {
        requireTeacher(principal);
        scheduleUpdateService.updateSchedule(classId, scheduleId, request);
        return ApiResponse.ok();
    }

    // 일정 삭제 (선생님)
    @DeleteMapping("/schedule/{classId}/{scheduleId}")
    public ApiResponse<Void> deleteSchedule(
            @AuthenticationPrincipal JwtPrincipal principal,
            @PathVariable Long classId,
            @PathVariable Long scheduleId) {
        requireTeacher(principal);
        scheduleDeleteService.deleteSchedule(classId, scheduleId);
        return ApiResponse.ok();
    }

    private void requireTeacher(JwtPrincipal principal) {
        if (principal.role() != Role.TEACHER) {
            throw new RoleAccessDeniedException();
        }
    }
}
