package com.option.refit.domain.absence.controller;

import com.option.refit.domain.absence.dto.AbsenceRequest;
import com.option.refit.domain.absence.dto.AbsenceResponse;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/absences")
@RequiredArgsConstructor
public class AbsenceController {

    private final AbsenceRequestService absenceRequestService;
    private final AbsenceCancelService absenceCancelService;
    private final AbsenceApproveService absenceApproveService;

    // 결석 신청 (학생)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AbsenceResponse> requestAbsence(
            @AuthenticationPrincipal JwtPrincipal principal,
            @Valid @RequestBody AbsenceRequest request) {
        if (principal.role() != Role.STUDENT) {
            throw new RoleAccessDeniedException();
        }
        return ApiResponse.ok(absenceRequestService.requestAbsence(principal.userId(), request));
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
