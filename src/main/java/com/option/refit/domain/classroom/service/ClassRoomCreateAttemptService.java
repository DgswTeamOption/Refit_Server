package com.option.refit.domain.classroom.service;

import com.option.refit.domain.classroom.dto.request.ClassRoomCreateRequest;
import com.option.refit.domain.classroom.dto.response.ClassRoomResponse;
import com.option.refit.domain.classroom.entity.ClassRoomEntity;
import com.option.refit.domain.classroom.repository.ClassRoomRepository;
import com.option.refit.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClassRoomCreateAttemptService {

    private final ClassRoomRepository classroomRepository;

    // 독립적인 트랜잭션에서 수업을 저장
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ClassRoomResponse create(
            ClassRoomCreateRequest request,
            User user,
            String classCode
    ) {
        ClassRoomEntity classroom = ClassRoomEntity.create(
                request.name(), user, classCode
        );

        ClassRoomEntity savedClassroom =
                classroomRepository.saveAndFlush(classroom);

        return ClassRoomResponse.from(savedClassroom);
    }
}