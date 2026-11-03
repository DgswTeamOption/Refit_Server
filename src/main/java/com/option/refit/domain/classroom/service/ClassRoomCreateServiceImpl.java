package com.option.refit.domain.classroom.service;

import com.option.refit.domain.classroom.dto.request.ClassRoomCreateRequest;
import com.option.refit.domain.classroom.dto.response.ClassRoomResponse;
import com.option.refit.domain.classroom.entity.ClassRoomEntity;
import com.option.refit.domain.classroom.repository.ClassRoomRepository;
import com.option.refit.domain.user.entity.Role;
import com.option.refit.domain.user.entity.User;
import com.option.refit.global.error.classError.exception.ClassPermissionDeniedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClassRoomCreateServiceImpl
        implements ClassRoomCreateService {

    private final ClassRoomRepository classroomRepository;

    @Override
    @Transactional
    public ClassRoomResponse createClassroom(
            ClassRoomCreateRequest request,
            User user
    ) {

        if (user.getRole() != Role.TEACHER) {
            throw new ClassPermissionDeniedException();
        }

        ClassRoomEntity classroom = ClassRoomEntity.create(
                request.name(),
                user
        );

        ClassRoomEntity savedClassroom =
                classroomRepository.save(classroom);

        return ClassRoomResponse.from(savedClassroom);
    }
}
