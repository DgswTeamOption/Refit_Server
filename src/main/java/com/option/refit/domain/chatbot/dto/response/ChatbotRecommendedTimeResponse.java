package com.option.refit.domain.chatbot.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

// 추천 보강 시간대 (신청자 수 내림차순 순위)
public record ChatbotRecommendedTimeResponse(
        int rank,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime makeupTime,
        String dayOfWeek,
        long applicantCount
) {}
