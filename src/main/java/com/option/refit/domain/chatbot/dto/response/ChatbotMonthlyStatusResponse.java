package com.option.refit.domain.chatbot.dto.response;

// 이번 달 결석·보강 현황
public record ChatbotMonthlyStatusResponse(
        int year,
        int month,
        long absenceCount,
        long makeupCount,
        long pendingMakeupCount
) {}
