package com.option.refit.domain.nfc.controller;

import com.option.refit.domain.attendance.dto.AttendanceResponse;
import com.option.refit.domain.nfc.dto.NfcAttendanceRequest;
import com.option.refit.domain.nfc.dto.NfcTagCreateRequest;
import com.option.refit.domain.nfc.dto.NfcTagLinkRequest;
import com.option.refit.domain.nfc.dto.NfcTagResponse;
import com.option.refit.domain.nfc.dto.NfcTagUpdateRequest;
import com.option.refit.domain.nfc.service.NfcCheckInService;
import com.option.refit.domain.nfc.service.NfcService;
import com.option.refit.global.common.ApiResponse;
import com.option.refit.global.jwt.JwtPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/class/nfc")
@RequiredArgsConstructor
public class NfcController {

    private final NfcService nfcService;
    private final NfcCheckInService nfcCheckInService;

    // NFC 출석 (학생) - 태그에서 읽은 token과 출석할 회차 schedule_id. URL을 GET으로 열기만 해서는 저장되지 않고 이 POST만 저장한다.
    // 201 성공 / 400 입력값 오류·출석 가능 시간 아님·태그와 회차의 수업 불일치·비활성/미연결 태그
    // 401 미인증 / 403 학생 아님·수업 미수강·다른 학생의 회차 / 404 없는 태그·회차
    // 409 이미 출석 처리됨·선생님이 결석 처리한 회차
    @PostMapping("/attendance")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AttendanceResponse> checkIn(
            @AuthenticationPrincipal JwtPrincipal principal,
            @Valid @RequestBody NfcAttendanceRequest request) {
        return ApiResponse.ok(nfcCheckInService.checkIn(principal, request));
    }

    // NFC 태그 등록 (선생님) - 서버가 토큰과 출석 URL을 만든다. 스티커에 쓰는 것은 앱·웹·쓰기 도구의 몫
    // 201 성공 / 400 입력값 오류 / 401 미인증 / 403 선생님 아님
    @Operation(description = "선생님 전용. 토큰과 출석 URL은 이 응답에서만 내려가므로 바로 스티커에 기록해야 합니다.")
    @PostMapping("/tags")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<NfcTagResponse> register(
            @AuthenticationPrincipal JwtPrincipal principal,
            @Valid @RequestBody NfcTagCreateRequest request) {
        return ApiResponse.ok(nfcService.register(principal, request));
    }

    // 내 태그 목록 (선생님)
    // 200 성공 / 401 미인증 / 403 선생님 아님
    @Operation(description = "선생님 전용. 본인 소유 태그만 조회되며 토큰과 URL은 포함되지 않습니다.")
    @GetMapping("/tags")
    public ApiResponse<List<NfcTagResponse>> getMyTags(@AuthenticationPrincipal JwtPrincipal principal) {
        return ApiResponse.ok(nfcService.getMyTags(principal));
    }

    // 태그 단건 조회 (소유 선생님)
    // 200 성공 / 401 미인증 / 403 선생님 아님·다른 선생님의 태그 / 404 없는 태그
    @Operation(description = "선생님 전용. 본인 소유 태그만 조회할 수 있으며 토큰과 URL은 포함되지 않습니다.")
    @GetMapping("/tags/{tagId}")
    public ApiResponse<NfcTagResponse> getTag(
            @AuthenticationPrincipal JwtPrincipal principal,
            @PathVariable Long tagId) {
        return ApiResponse.ok(nfcService.getTag(principal, tagId));
    }

    // 태그 수정 (소유 선생님) - 이름, is_active 중 보낸 값만 변경
    // 200 성공 / 400 입력값 오류 / 401 미인증 / 403 선생님 아님·다른 선생님의 태그 / 404 없는 태그
    @Operation(description = "선생님 전용. 본인 소유 태그만 수정할 수 있습니다.")
    @PatchMapping("/tags/{tagId}")
    public ApiResponse<NfcTagResponse> update(
            @AuthenticationPrincipal JwtPrincipal principal,
            @PathVariable Long tagId,
            @Valid @RequestBody NfcTagUpdateRequest request) {
        return ApiResponse.ok(nfcService.update(principal, tagId, request));
    }

    // 태그 삭제 (소유 선생님, 소프트 삭제) - 수업에 연결된 태그는 먼저 연결 해제 필요
    // 200 성공 / 401 미인증 / 403 선생님 아님·다른 선생님의 태그 / 404 없는 태그 / 409 수업에 연결된 태그
    @Operation(description = "선생님 전용. 본인 소유 태그만 삭제할 수 있습니다.")
    @DeleteMapping("/tags/{tagId}")
    public ApiResponse<Void> delete(
            @AuthenticationPrincipal JwtPrincipal principal,
            @PathVariable Long tagId) {
        nfcService.delete(principal, tagId);
        return ApiResponse.ok();
    }

    // 수업에 연결된 태그 조회 (담당 선생님)
    // 200 성공 / 401 미인증 / 403 선생님 아님·담당 수업 아님 / 404 없는 수업·연결된 태그 없음
    @Operation(description = "선생님 전용. 담당 수업의 태그만 조회할 수 있으며 토큰과 URL은 포함되지 않습니다.")
    @GetMapping("/{classId}")
    public ApiResponse<NfcTagResponse> getLinkedTag(
            @AuthenticationPrincipal JwtPrincipal principal,
            @PathVariable Long classId) {
        return ApiResponse.ok(nfcService.getLinkedTag(principal, classId));
    }

    // 태그를 수업에 연결 (담당 선생님 + 태그 소유자) - 기존 연결을 덮어쓰지 않는다
    // 200 성공 / 400 입력값 오류 / 401 미인증 / 403 선생님 아님·담당 수업 아님·다른 선생님의 태그
    // 404 없는 수업·태그 / 409 이미 연결된 태그·이미 태그가 있는 수업
    @Operation(description = "선생님 전용. 담당 수업과 본인 소유 태그만 연결할 수 있습니다.")
    @PostMapping("/{classId}")
    public ApiResponse<NfcTagResponse> link(
            @AuthenticationPrincipal JwtPrincipal principal,
            @PathVariable Long classId,
            @Valid @RequestBody NfcTagLinkRequest request) {
        return ApiResponse.ok(nfcService.link(principal, classId, request.getTagId()));
    }

    // 수업의 태그 연결 해제 (담당 선생님) - 태그와 과거 출석 기록은 그대로 남는다
    // 200 성공 / 401 미인증 / 403 선생님 아님·담당 수업 아님 / 404 없는 수업·연결된 태그 없음
    @Operation(description = "선생님 전용. 담당 수업의 태그 연결만 해제할 수 있습니다.")
    @DeleteMapping("/{classId}")
    public ApiResponse<Void> unlink(
            @AuthenticationPrincipal JwtPrincipal principal,
            @PathVariable Long classId) {
        nfcService.unlink(principal, classId);
        return ApiResponse.ok();
    }
}
