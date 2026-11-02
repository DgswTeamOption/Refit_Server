package com.option.refit.domain.classroom.dto.response;

import com.option.refit.domain.classroom.entity.ClassRoomEntity;

import java.time.LocalDateTime;

// 수업 홈에서 보여줄 기본 정보


public class ClassRoomHomeResponse {


    public record ClassroomHomeResponse(
            Long classroomId,
            String name,
            String classCode,
            String teacherName,
            long memberCount,
            LocalDateTime createdAt
    ) {

        public static ClassroomHomeResponse from(
                ClassRoomEntity classroom,
                long memberCount
        ) {
            return new ClassroomHomeResponse(
                    classroom.getId(),
                    classroom.getName(),
                    classroom.getClassCode(),
                    classroom.getTeacher().getName(),
                    memberCount,
                    classroom.getCreatedAt()
            );
        }
    }

}

