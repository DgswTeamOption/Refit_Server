package com.option.refit.domain.chatbot.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// 챗봇 자유 텍스트 입력. classId는 시간 추천 시에만 쓰는 선택값 (없으면 선생님의 전체 수업 기준)
public record ChatbotQueryRequest(
        @NotBlank(message = "메시지를 입력해주세요")
        @Size(max = 200, message = "메시지는 200자 이하로 입력해주세요")
        String message,

        Long classId
) {}
