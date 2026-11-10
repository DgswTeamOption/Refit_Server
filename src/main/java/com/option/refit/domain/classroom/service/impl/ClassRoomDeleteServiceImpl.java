
package com.option.refit.domain.classroom.service.impl;

import com.option.refit.domain.classroom.entity.ClassRoomEntity;
import com.option.refit.domain.classroom.repository.ClassRoomRepository;
import com.option.refit.domain.classroom.service.ClassRoomDeleteService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ClassRoomDeleteServiceImpl implements ClassRoomDeleteService {

    private final ClassRoomRepository classRoomRepository;

    @Override
    public void deleteClassRoom(Long classId, Long teacherId) {

        ClassRoomEntity classRoom = classRoomRepository.findById(classId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 수업입니다."));

        // 수업 생성 교사인지 확인
        if (!classRoom.getTeacher().getId().equals(teacherId)) {
            throw new IllegalArgumentException("수업 삭제 권한이 없습니다.");
        }

        // @SQLDelete에 의해 소프트 삭제 실행
        classRoomRepository.delete(classRoom);
    }
}
