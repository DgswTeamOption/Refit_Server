package com.option.refit.domain.classroom.service.impl;

import com.option.refit.domain.classroom.dto.request.ClassRoomCreateRequest;
import com.option.refit.domain.classroom.dto.response.ClassRoomResponse;
import com.option.refit.domain.classroom.entity.ClassRoomEntity;
import com.option.refit.domain.classroom.repository.ClassRoomRepository;
import com.option.refit.domain.classroom.service.ClassRoomCreateService;
import com.option.refit.domain.user.entity.Role;
import com.option.refit.domain.user.entity.User;
import com.option.refit.global.error.classError.exception.ClassPermissionDeniedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClassRoomCreateServiceImpl implements ClassRoomCreateService {

    // 수업 정보를 DB에 저장하고 조회하기 위한 Repository
    private final ClassRoomRepository classroomRepository;

    @Override
    @Transactional
    public ClassRoomResponse createClassroom(ClassRoomCreateRequest request, User user) {

        // 수업 생성 요청을 보낸 사용자가 교사인지 확인
        if (user.getRole() != Role.TEACHER) {
            throw new ClassPermissionDeniedException();
        }

        String classCode;

        // 중복되지 않는 8자리 수업 참여 코드 생성
        do {
            classCode = UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, 8)
                    .toUpperCase();

            // 이미 사용 중인 코드라면 새로운 코드 생성
        } while (classroomRepository.existsByClassCode(classCode));

        // 전달받은 수업 이름과 교사 정보, 생성한 참여 코드로 수업 Entity 생성
        ClassRoomEntity classroom = ClassRoomEntity.create(
                request.name(), user, classCode
        );

        // 생성한 수업 정보를 DB에 저장
        ClassRoomEntity savedClassroom = classroomRepository.save(classroom);

        // 저장된 Entity를 응답 DTO로 변환하여 반환
        return ClassRoomResponse.from(savedClassroom);
    }
}
