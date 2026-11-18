package com.option.refit.domain.chatbot.dto.response;

import java.time.LocalDate;

// 학생 검색 결과 한 건 (수업별로 한 건씩, 같은 학생이 여러 수업에 있으면 여러 건)
public record ChatbotStudentResponse(
        Long studentId,
        String name,
        String profileImageUrl,
        Long classId,
        String className,
        long absenceCount,
        long makeupCount,
        LocalDate lastAbsenceDate
) {}
