package com.option.refit.domain.classroom.dto.response;

import java.time.LocalDate;

// 수업에 참여 중인 학생 한 명의 정보와 결석·보강 현황
public record ClassRoomStudentResponse(
        Long studentId,
        String name,
        String profileImageUrl,
        long makeupCount,
        long absenceCount,
        LocalDate lastAbsenceDate
) {
}
