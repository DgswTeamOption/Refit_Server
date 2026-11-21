package com.option.refit.domain.schedule.controller;

import com.option.refit.domain.schedule.dto.ScheduleRuleRequest.ScheduleRuleCreateRequest;
import com.option.refit.domain.schedule.dto.ScheduleRuleRequest.ScheduleRuleUpdateRequest;
import com.option.refit.domain.schedule.dto.ScheduleRuleResponse;
import com.option.refit.domain.schedule.service.ScheduleRuleService;
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

// 요일 반복 일정(예: 월요일 16:00-17:00). 등록하면 오늘부터 8주치 회차가 자동으로 만들어진다.
@RestController
@RequestMapping("/api/class/schedule-rules")
@RequiredArgsConstructor
public class ScheduleRuleController {

    private final ScheduleRuleService scheduleRuleService;

    // 반복 일정 목록 (선생님: 수업 전체, 학생: 본인 것만)
    @GetMapping("/{classId}")
    public ApiResponse<List<ScheduleRuleResponse>> getRules(
            @AuthenticationPrincipal JwtPrincipal principal,
            @PathVariable Long classId) {
        return ApiResponse.ok(scheduleRuleService.list(principal, classId));
    }

    // 반복 일정 등록 (담당 선생님). 같은 요일에 시간이 겹치면 409
    @PostMapping("/{classId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Long> createRule(
            @AuthenticationPrincipal JwtPrincipal principal,
            @PathVariable Long classId,
            @Valid @RequestBody ScheduleRuleCreateRequest request) {
        requireTeacher(principal);
        return ApiResponse.ok(scheduleRuleService.create(principal, classId, request));
    }

    // 반복 일정 수정 (담당 선생님). 아직 시작 전인 회차만 새 요일/시간으로 다시 만들어진다
    @PatchMapping("/{classId}/{ruleId}")
    public ApiResponse<Void> updateRule(
            @AuthenticationPrincipal JwtPrincipal principal,
            @PathVariable Long classId,
            @PathVariable Long ruleId,
            @Valid @RequestBody ScheduleRuleUpdateRequest request) {
        requireTeacher(principal);
        scheduleRuleService.update(principal, classId, ruleId, request);
        return ApiResponse.ok();
    }

    // 반복 일정 삭제 (담당 선생님). 아직 시작 전인 회차만 함께 삭제되고 지난 회차와 출석 기록은 남는다
    @DeleteMapping("/{classId}/{ruleId}")
    public ApiResponse<Void> deleteRule(
            @AuthenticationPrincipal JwtPrincipal principal,
            @PathVariable Long classId,
            @PathVariable Long ruleId) {
        requireTeacher(principal);
        scheduleRuleService.delete(principal, classId, ruleId);
        return ApiResponse.ok();
    }

    private void requireTeacher(JwtPrincipal principal) {
        if (principal.role() != Role.TEACHER) {
            throw new RoleAccessDeniedException();
        }
    }
}
