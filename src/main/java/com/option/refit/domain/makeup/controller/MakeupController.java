package com.option.refit.domain.makeup.controller;

import com.option.refit.domain.makeup.dto.*;
import com.option.refit.domain.makeup.service.MakeupApproveService;
import com.option.refit.domain.makeup.service.MakeupAvailabilityCreateService;
import com.option.refit.domain.makeup.service.MakeupCancelService;
import com.option.refit.domain.makeup.service.MakeupGetService;
import com.option.refit.domain.makeup.service.MakeupRequestService;
import com.option.refit.global.common.ApiResponse;
import com.option.refit.global.jwt.JwtPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 경로는 API 명세서(보강 관리) 기준
@RestController
@RequiredArgsConstructor
public class MakeupController {

    private final MakeupAvailabilityCreateService availabilityCreateService;
    private final MakeupRequestService makeupRequestService;
    private final MakeupApproveService makeupApproveService;
    private final MakeupCancelService makeupCancelService;
    private final MakeupGetService makeupGetService;

    // 보강 가능 시간 등록 (선생님)
    @PostMapping("/api/makeups/availability")
    public ApiResponse<List<MakeupAvailabilityResponse>> createAvailability(
            @AuthenticationPrincipal JwtPrincipal principal,
            @Valid @RequestBody MakeupAvailabilityRequest request) {
        return ApiResponse.ok(availabilityCreateService.createAvailabilities(principal, request));
    }

    // 보강 일정 선택 및 신청 (학생)
    @PostMapping("/api/makeups/request")
    public ApiResponse<MakeupResponse> requestMakeup(
            @AuthenticationPrincipal JwtPrincipal principal,
            @Valid @RequestBody MakeupRequest request) {
        return ApiResponse.ok(makeupRequestService.requestMakeup(principal, request));
    }

    // 보강 신청 승인 / 거절 (선생님)
    @PatchMapping("/api/makeups/approve/{makeupId}")
    public ApiResponse<MakeupApproveResponse> approveMakeup(
            @AuthenticationPrincipal JwtPrincipal principal,
            @PathVariable Long makeupId,
            @Valid @RequestBody MakeupApproveRequest request) {
        return ApiResponse.ok(makeupApproveService.processMakeup(principal, makeupId, request));
    }

    // 보강 신청 취소 (신청한 학생 본인, 대기 상태만)
    @PatchMapping("/api/makeups/cancel/{makeupId}")
    public ApiResponse<MakeupApproveResponse> cancelMakeup(
            @AuthenticationPrincipal JwtPrincipal principal,
            @PathVariable Long makeupId) {
        return ApiResponse.ok(makeupCancelService.cancelMakeup(principal, makeupId));
    }

    // 보강 목록 조회 (로그인)
    @GetMapping("/api/class/makeups/{classId}")
    public ApiResponse<List<MakeupResponse>> getMakeups(
            @AuthenticationPrincipal JwtPrincipal principal,
            @PathVariable Long classId,
            @RequestParam(name = "approval_status", required = false) Integer approvalStatus) {
        return ApiResponse.ok(makeupGetService.getMakeups(principal, classId, approvalStatus));
    }

    // 보강 일정 조회 (로그인)
    @GetMapping("/api/class/makeups/schedule/{classId}")
    public ApiResponse<List<MakeupScheduleResponse>> getMakeupSchedules(
            @AuthenticationPrincipal JwtPrincipal principal,
            @PathVariable Long classId,
            @RequestParam(name = "student_id", required = false) Long studentId,
            @RequestParam(name = "schedule_id", required = false) Long scheduleId,
            @RequestParam(name = "makeup_time", required = false) String makeupTime) {
        return ApiResponse.ok(makeupGetService.getMakeupSchedules(principal, classId, studentId, scheduleId, makeupTime));
    }
}
