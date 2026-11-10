package com.option.refit.domain.classroom.service.impl;

import com.option.refit.domain.classroom.dto.response.ClassRoomStudentResponse;
import com.option.refit.domain.classroom.entity.ClassRoomEntity;
import com.option.refit.domain.classroom.repository.ClassRoomRepository;
import com.option.refit.domain.classroom.repository.ClassRoomMemberRepository;
import com.option.refit.domain.classroom.service.ClassRoomStudentService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClassRoomStudentServiceImpl implements ClassRoomStudentService {

    private final ClassRoomRepository classRoomRepository;
    private final ClassRoomMemberRepository classRoomMemberRepository;

    @Override
    public List<ClassRoomStudentResponse> getStudents(Long classId) {

        ClassRoomEntity classRoom = classRoomRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 수업입니다."));

        // 해당 수업에 참여 중인 학생 조회
        // leftAt이 존재하는 학생은 제외
        // 학생별 결석 및 보강 통계 조회 후 DTO 변환

        return List.of();
    }
}
