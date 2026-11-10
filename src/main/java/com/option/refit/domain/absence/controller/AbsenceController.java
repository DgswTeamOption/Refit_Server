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
    // 201 성공 / 400 지난 날짜·입력값 오류 / 403 학생 아님·수업 미참여 / 404 없는 수업 / 409 같은 날짜 중복 신청
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

    // 내 결석 신청 목록 (학생, 본인 것만, 취소 건 포함)
    // 200 성공 / 403 학생 아님 / 404 없는 수업
    @GetMapping("/{classId}/me")
    public ApiResponse<List<AbsenceListResponse>> getMyAbsences(
            @AuthenticationPrincipal JwtPrincipal principal,
            @PathVariable Long classId) {
        return ApiResponse.ok(absenceListService.getMyAbsences(principal, classId));
    }

    // 수업 결석 신청 목록 (담당 선생님만, status/year/month 선택 필터)
    // 응답에 student_name 포함, status는 PENDING/APPROVED/REJECTED/CANCELED (필터 없으면 전체)
    // 200 성공 / 400 잘못된 status·month / 403 담당 선생님 아님 / 404 없는 수업
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
    // 200 성공 / 403 본인 신청 아님 / 404 없는 신청 / 409 이미 처리(승인·반려·취소)된 신청
    @DeleteMapping("/{absenceId}")
    public ApiResponse<Void> cancelAbsence(
            @AuthenticationPrincipal JwtPrincipal principal,
            @PathVariable Long absenceId) {
        absenceCancelService.cancelAbsence(principal.userId(), absenceId);
        return ApiResponse.ok();
    }

    // 결석 승인 (해당 수업 담당 선생님)
    // 200 성공 / 403 선생님 아님·담당 수업 아님 / 404 없는 신청 / 409 이미 처리된 신청
    @PatchMapping("/{absenceId}/approve")
    public ApiResponse<AbsenceResponse> approveAbsence(
            @AuthenticationPrincipal JwtPrincipal principal,
            @PathVariable Long absenceId) {
        return ApiResponse.ok(absenceApproveService.approve(principal, absenceId));
    }

    // 결석 반려 (해당 수업 담당 선생님) - 상태 코드는 승인과 동일
    @PatchMapping("/{absenceId}/reject")
    public ApiResponse<AbsenceResponse> rejectAbsence(
            @AuthenticationPrincipal JwtPrincipal principal,
            @PathVariable Long absenceId) {
        return ApiResponse.ok(absenceApproveService.reject(principal, absenceId));
    }
}
