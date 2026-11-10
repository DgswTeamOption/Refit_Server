package com.option.refit.domain.classroom.dto.response;

import com.option.refit.domain.classroom.entity.ClassRoomEntity;
import com.option.refit.domain.schedule.dto.ScheduleResponse;

import java.time.LocalDateTime;
import java.util.List;

// 수업 홈에서 보여줄 기본 정보와 월별 캘린더 정보
public record ClassRoomHomeResponse(
        Long classroomId,
        String name,
        String classCode,
        String teacherName,
        long memberCount,
        LocalDateTime createdAt,
        List<ScheduleResponse> calendar
) {

    public static ClassRoomHomeResponse from(
            ClassRoomEntity classroom,
            long memberCount,
            List<ScheduleResponse> calendar,
            boolean isTeacher
    ) {
        return new ClassRoomHomeResponse(
                classroom.getId(),
                classroom.getName(),
                isTeacher ? classroom.getClassCode() : null,
                classroom.getTeacher().getName(),
                memberCount,
                classroom.getCreatedAt(),
                calendar
        );
    }
}
