package com.option.refit.domain.classroom.service.impl;

import com.option.refit.domain.classroom.dto.request.ClassRoomJoinRequest;
import com.option.refit.domain.classroom.dto.response.ClassRoomResponse;
import com.option.refit.domain.classroom.entity.ClassRoomEntity;
import com.option.refit.domain.classroom.entity.ClassRoomMemberEntity;
import com.option.refit.domain.classroom.repository.ClassRoomMemberRepository;
import com.option.refit.domain.classroom.repository.ClassRoomRepository;
import com.option.refit.domain.classroom.service.ClassRoomJoinService;
import com.option.refit.domain.user.entity.Role;
import com.option.refit.domain.user.entity.User;
import com.option.refit.global.error.classError.exception.AlreadyJoinedClassException;
import com.option.refit.global.error.classError.exception.ClassPermissionDeniedException;
import com.option.refit.global.error.classError.exception.InvalidClassCodeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 학생이 수업에서 나간 뒤 다시 참여할 경우 새로운 데이터를 무조건 추가하지 않고 기존 참여 기록을 복구할 수 있도록 함

@Service
@RequiredArgsConstructor
public class ClassRoomJoinServiceImpl
        implements ClassRoomJoinService {

    private final ClassRoomRepository classroomRepository;
    private final ClassRoomMemberRepository memberRepository;

    @Override
    @Transactional
    public ClassRoomResponse joinClassroom(
            ClassRoomJoinRequest request,
            User user
    ) {

        // 학생인지 확인
        if (user.getRole() != Role.STUDENT) {
            throw new ClassPermissionDeniedException();
        }

        // 수업 코드 정리
        String classCode = request.classCode();

        // 수업 코드로 수업 조회
        ClassRoomEntity classroom = classroomRepository
                .findByClassCode(classCode)
                .orElseThrow(InvalidClassCodeException::new);

        // 기존 참여 기록 확인
        var existingMember = memberRepository
                .findByClassroom_IdAndUser_Id(
                        classroom.getId(),
                        user.getId()
                );

        if (existingMember.isPresent()) {

            ClassRoomMemberEntity member = existingMember.get();

            // 이미 참여 중이면 예외 발생
            if (member.isActive()) {
                throw new AlreadyJoinedClassException();
            }

            // 이전에 나간 수업이면 재참여
            member.rejoin();

        } else {

            // 처음 참여하는 경우
            ClassRoomMemberEntity member =
                    ClassRoomMemberEntity.create(classroom, user);

            memberRepository.save(member);
        }

        return ClassRoomResponse.from(classroom);
    }
}
