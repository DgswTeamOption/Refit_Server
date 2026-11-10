package com.option.refit.domain.classroom.dto.response;

import java.time.LocalDate;

// 수업 상세 화면에서 학생별 출결 및 보강 통계를 전달하는 응답 DTO

public record ClassRoomStudentResponse(
        Long studentId,
        String studentName,
        long makeupCount,
        long absenceCount,
        LocalDate recentAbsenceDate,
        boolean makeupRequired,
        long makeupRequiredDays
) { }
