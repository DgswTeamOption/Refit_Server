package com.option.refit.domain.classroom.dto.response;

import com.option.refit.domain.classroom.entity.ClassRoomEntity;
import java.time.LocalDateTime;

// 수업 생성 및 참여 결과 응답 DTO
public record ClassRoomResponse(
        Long id,
        String name,
        String classCode,
        String teacherName,
        LocalDateTime createdAt
) {

    // Entity → Response DTO 변환
    public static ClassRoomResponse from(ClassRoomEntity classroom) {
        return new ClassRoomResponse(
                classroom.getId(),
                classroom.getName(),
                classroom.getClassCode(),
                classroom.getTeacher().getName(),
                classroom.getCreatedAt()
        );
    }
}