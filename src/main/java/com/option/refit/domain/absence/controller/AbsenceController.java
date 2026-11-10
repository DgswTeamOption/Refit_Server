package com.option.refit.domain.absence.controller;

import com.option.refit.domain.absence.dto.AbsenceRequest;
import com.option.refit.domain.absence.dto.AbsenceListResponse;
import com.option.refit.domain.absence.dto.AbsenceResponse;
import com.option.refit.domain.absence.entity.AbsenceStatus;
import com.option.refit.domain.absence.service.AbsenceListService;
import com.option.refit.domain.absence.service.AbsenceApproveService;
import com.option.refit.domain.absence.service.AbsenceCancelService;
import com.option.refit.domain.absence.service.AbsenceRequestService;
import com.option.refit.domain.user.entity.Role;
import com.option.refit.global.common.ApiResponse;
import com.option.refit.global.error.securityError.exception.RoleAccessDeniedException;
import com.option.refit.global.jwt.JwtPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/class/absences")
@RequiredArgsConstructor
public class AbsenceController {

    private final AbsenceRequestService absenceRequestService;
    private final AbsenceCancelService absenceCancelService;
    private final AbsenceApproveService absenceApproveService;
    private final AbsenceListService absenceListService;

    // 결석 신청 (학생) - 명세: POST api/class/absences/{classId}
    @PostMapping("/{classId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AbsenceResponse> requestAbsence(
            @AuthenticationPrincipal JwtPrincipal principal,
            @PathVariable Long classId,
            @Valid @RequestBody AbsenceRequest request) {
        if (principal.role() != Role.STUDENT) {
            throw new RoleAccessDeniedException();
        }
        return ApiResponse.ok(absenceRequestService.requestAbsence(principal.userId(), classId, request));
    }

    // 내 결석 신청 목록 (학생, 본인 것만)
    @GetMapping("/{classId}/me")
    public ApiResponse<List<AbsenceListResponse>> getMyAbsences(
            @AuthenticationPrincipal JwtPrincipal principal,
            @PathVariable Long classId) {
        return ApiResponse.ok(absenceListService.getMyAbsences(principal, classId));
    }

    // 수업 결석 신청 목록 (담당 선생님만, status/year/month 선택 필터)
    @GetMapping("/{classId}")
    public ApiResponse<List<AbsenceListResponse>> getClassAbsences(
            @AuthenticationPrincipal JwtPrincipal principal,
            @PathVariable Long classId,
            @RequestParam(required = false) AbsenceStatus status,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        return ApiResponse.ok(absenceListService.getClassAbsences(principal, classId, status, year, month));
    }

    // 결석 신청 취소 (신청자 본인, 승인 전만)
    @DeleteMapping("/{absenceId}")
    public ApiResponse<Void> cancelAbsence(
            @AuthenticationPrincipal JwtPrincipal principal,
            @PathVariable Long absenceId) {
        absenceCancelService.cancelAbsence(principal.userId(), absenceId);
        return ApiResponse.ok();
    }

    // 결석 승인 (선생님)
    @PatchMapping("/{absenceId}/approve")
    public ApiResponse<AbsenceResponse> approveAbsence(
            @AuthenticationPrincipal JwtPrincipal principal,
            @PathVariable Long absenceId) {
        return ApiResponse.ok(absenceApproveService.approve(principal, absenceId));
    }

    // 결석 반려 (선생님)
    @PatchMapping("/{absenceId}/reject")
    public ApiResponse<AbsenceResponse> rejectAbsence(
            @AuthenticationPrincipal JwtPrincipal principal,
            @PathVariable Long absenceId) {
        return ApiResponse.ok(absenceApproveService.reject(principal, absenceId));
    }
}
