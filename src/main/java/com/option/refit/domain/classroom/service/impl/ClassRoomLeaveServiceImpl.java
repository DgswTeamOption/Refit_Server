
package com.option.refit.domain.classroom.service.impl;

import com.option.refit.domain.classroom.entity.ClassRoomEntity;
import com.option.refit.domain.classroom.entity.ClassRoomMemberEntity;
import com.option.refit.domain.classroom.repository.ClassRoomMemberRepository;
import com.option.refit.domain.classroom.repository.ClassRoomRepository;
import com.option.refit.domain.classroom.service.ClassRoomLeaveService;
import com.option.refit.domain.user.entity.User;
import com.option.refit.global.error.classError.exception.ClassAccessDeniedException;
import com.option.refit.global.error.classError.exception.ClassNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 실제 데이터 삭제 대신 leftAt 을 기록하는 방식

@Service
@RequiredArgsConstructor
public class ClassRoomLeaveServiceImpl
        implements ClassRoomLeaveService {

    private final ClassRoomRepository classroomRepository;
    private final ClassRoomMemberRepository memberRepository;

    @Override
    @Transactional
    public void leaveClassroom(Long classroomId, User user) {

        // 수업이 존재하는지 확인
        ClassRoomEntity classroom = classroomRepository
                .findById(classroomId)
                .orElseThrow(ClassNotFoundException::new);

        // 교사는 자신이 생성한 수업에서 나갈 수 없도록 처리
        if (classroom.getTeacher().getId().equals(user.getId())) {
            throw new ClassAccessDeniedException();
        }

        // 수업 참여 기록 조회
        ClassRoomMemberEntity member = memberRepository
                .findByClassroom_IdAndUser_Id(
                        classroomId,
                        user.getId()
                )
                .filter(ClassRoomMemberEntity::isActive)
                .orElseThrow(ClassAccessDeniedException::new);

        // 수업 나가기 처리
        member.leave();
    }
}
