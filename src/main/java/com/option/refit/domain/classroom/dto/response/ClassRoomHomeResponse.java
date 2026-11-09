package com.option.refit.domain.classroom.dto.response;

import com.option.refit.domain.classroom.entity.ClassRoomEntity;

import java.time.LocalDateTime;

// 수업 홈에서 보여줄 기본 정보
public record ClassRoomHomeResponse(
        Long classroomId,
        String name,
        String classCode,
        String teacherName,
        long memberCount,
        LocalDateTime createdAt
) {

    // Entity를 수업 홈 응답 DTO로 변환
    public static ClassRoomHomeResponse from(
            ClassRoomEntity classroom,
            long memberCount
    ) {
        return new ClassRoomHomeResponse(
                classroom.getId(),
                classroom.getName(),
                classroom.getClassCode(),
                classroom.getTeacher().getName(),
                memberCount,
                classroom.getCreatedAt()
        );
    }
}
