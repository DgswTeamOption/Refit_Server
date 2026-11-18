package com.option.refit.domain.chatbot.dto.response;

import java.time.LocalDate;

// 결석은 있지만 보강 신청(제안)이 아직 없는 학생
public record ChatbotUnproposedStudentResponse(
        Long studentId,
        String name,
        String profileImageUrl,
        Long classId,
        String className,
        long absenceCount,
        LocalDate lastAbsenceDate
) {}
