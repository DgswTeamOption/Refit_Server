package com.option.refit.domain.classroom.service.impl;

import com.option.refit.domain.classroom.entity.ClassRoomEntity;
import com.option.refit.domain.classroom.repository.ClassRoomRepository;
import com.option.refit.domain.classroom.service.ClassRoomDeleteService;
import com.option.refit.domain.user.entity.Role;
import com.option.refit.global.error.classError.exception.ClassAccessDeniedException;
import com.option.refit.global.error.classError.exception.ClassNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ClassRoomDeleteServiceImpl implements ClassRoomDeleteService {

    private final ClassRoomRepository classRoomRepository;

    @Override
    @Transactional
    public void deleteClassRoom(Long classId, Long teacherId) {

        // 삭제되지 않은 수업만 조회
        ClassRoomEntity classRoom = classRoomRepository.findById(classId)
                .orElseThrow(ClassNotFoundException::new);

        // 수업 생성 교사인지 확인
        if (!Objects.equals(classRoom.getTeacher().getId(), teacherId)) {
            throw new ClassAccessDeniedException();
        }

        // Soft Delete
        classRoomRepository.delete(classRoom);
    }
}