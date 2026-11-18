package com.option.refit.domain.chatbot.controller;

import com.option.refit.domain.chatbot.dto.request.ChatbotQueryRequest;
import com.option.refit.domain.chatbot.dto.response.ChatbotMonthlyStatusResponse;
import com.option.refit.domain.chatbot.dto.response.ChatbotQueryResponse;
import com.option.refit.domain.chatbot.dto.response.ChatbotRecommendedTimeResponse;
import com.option.refit.domain.chatbot.dto.response.ChatbotStudentResponse;
import com.option.refit.domain.chatbot.dto.response.ChatbotUnproposedStudentResponse;
import com.option.refit.domain.chatbot.service.ChatbotMonthlyStatusService;
import com.option.refit.domain.chatbot.service.ChatbotQueryService;
import com.option.refit.domain.chatbot.service.ChatbotRecommendTimeService;
import com.option.refit.domain.chatbot.service.ChatbotStudentSearchService;
import com.option.refit.domain.chatbot.service.ChatbotUnproposedStudentService;
import com.option.refit.global.common.ApiResponse;
import com.option.refit.global.jwt.JwtPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 선생님용 AI 챗봇 "Refit AI" (출결·보강 도우미). 모든 조회는 로그인한 선생님 본인 수업 데이터로 한정된다
@Tag(name = "Chatbot", description = "Refit AI 챗봇 (선생님 전용)")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chatbot")
public class ChatbotController {

    private final ChatbotStudentSearchService studentSearchService;
    private final ChatbotMonthlyStatusService monthlyStatusService;
    private final ChatbotUnproposedStudentService unproposedStudentService;
    private final ChatbotRecommendTimeService recommendTimeService;
    private final ChatbotQueryService queryService;

    @Operation(summary = "학생 검색",
            description = "본인 수업의 학생 중 이름에 name이 포함된 학생을 수업별로 반환합니다. name이 비어 있으면 400, 결과가 없으면 404, 학생 토큰이면 403입니다.")
    @GetMapping("/students")
    public ApiResponse<List<ChatbotStudentResponse>> searchStudents(
            @AuthenticationPrincipal JwtPrincipal principal,
            @RequestParam(required = false) String name
    ) {
        return ApiResponse.ok(studentSearchService.searchStudents(principal, name));
    }

    @Operation(summary = "이번 달 결석·보강 현황",
            description = "이번 달 승인된 결석 건수와 보강 건수(승인/대기)를 집계합니다. 보강은 보강 시각이 이번 달인 건 기준입니다.")
    @GetMapping("/monthly-status")
    public ApiResponse<ChatbotMonthlyStatusResponse> getMonthlyStatus(
            @AuthenticationPrincipal JwtPrincipal principal
    ) {
        return ApiResponse.ok(monthlyStatusService.getMonthlyStatus(principal));
    }

    @Operation(summary = "보강 미제안 학생 목록",
            description = "승인된 결석이 있지만 같은 수업에 거절·취소가 아닌 보강 신청이 하나도 없는 학생을 최근 결석 순으로 반환합니다. 없으면 빈 목록입니다.")
    @GetMapping("/unproposed-students")
    public ApiResponse<List<ChatbotUnproposedStudentResponse>> getUnproposedStudents(
            @AuthenticationPrincipal JwtPrincipal principal
    ) {
        return ApiResponse.ok(unproposedStudentService.getUnproposedStudents(principal));
    }

    @Operation(summary = "보강 시간 추천 Top3",
            description = "학생들이 신청한 (지나지 않은, 거절·취소 제외) 보강 시간대를 신청자 수 내림차순으로 최대 3개 반환합니다. 없는 수업이면 404, 본인 수업이 아니면 403, 집계할 신청이 없으면 404입니다.")
    @GetMapping("/recommend-time")
    public ApiResponse<List<ChatbotRecommendedTimeResponse>> recommendTime(
            @AuthenticationPrincipal JwtPrincipal principal,
            @RequestParam Long classId
    ) {
        return ApiResponse.ok(recommendTimeService.recommend(principal, classId));
    }

    @Operation(summary = "자유 텍스트 질문",
            description = "규칙 기반 라우팅입니다. 학생 이름 포함 → 학생 정보, '추천' → 시간 추천(classId 선택), '미제안' → 미제안 학생, '이번달'/'현황' → 월 현황, 그 외 → 안내 메시지. type으로 결과 화면을 구분합니다.")
    @PostMapping("/query")
    public ApiResponse<ChatbotQueryResponse> query(
            @AuthenticationPrincipal JwtPrincipal principal,
            @Valid @RequestBody ChatbotQueryRequest request
    ) {
        return ApiResponse.ok(queryService.query(principal, request));
    }
}
