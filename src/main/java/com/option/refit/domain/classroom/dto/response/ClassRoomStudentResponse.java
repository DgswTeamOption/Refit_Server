package com.option.refit.domain.classroom.dto.response;

import java.time.LocalDate;

public record ClassRoomStudentResponse(
        Long studentId,
        String studentName,
        long makeupCount,
        long absenceCount,
        LocalDate recentAbsenceDate,
        boolean makeupRequired,
        long makeupRequiredDays
) {}
