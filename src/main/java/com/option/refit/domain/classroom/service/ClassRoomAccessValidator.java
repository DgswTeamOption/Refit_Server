package com.option.refit.domain.classroom.service;

import com.option.refit.domain.classroom.entity.ClassRoomEntity;
import com.option.refit.domain.classroom.repository.ClassRoomMemberRepository;
import com.option.refit.domain.classroom.repository.ClassRoomRepository;
import com.option.refit.global.error.classError.exception.ClassAccessDeniedException;
import com.option.refit.global.error.classError.exception.ClassNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

// 수업 단위 API(일정 등)에서 공통으로 쓰는 수업 접근 권한 검사
// 수업이 없으면 404, 권한이 없으면 403
@Component
@RequiredArgsConstructor
public class ClassRoomAccessValidator {

    private final ClassRoomRepository classroomRepository;
    private final ClassRoomMemberRepository memberRepository;

    // 해당 수업을 생성한 담당 교사만 통과
    public void requireTeacher(Long classId, Long userId) {
        ClassRoomEntity classroom = getClassroom(classId);

        if (!isTeacherOf(classroom, userId)) {
            throw new ClassAccessDeniedException();
        }
    }

    // 현재 참여 중인 학생만 통과 (수업이 없으면 404)
    public void requireMember(Long classId, Long userId) {
        getClassroom(classId);

        if (!isActiveMember(classId, userId)) {
            throw new ClassAccessDeniedException();
        }
    }

    // 담당 교사이거나 현재 참여 중인 학생만 통과
    public void requireTeacherOrMember(Long classId, Long userId) {
        ClassRoomEntity classroom = getClassroom(classId);

        if (!isTeacherOf(classroom, userId) && !isActiveMember(classId, userId)) {
            throw new ClassAccessDeniedException();
        }
    }

    // 학생이 해당 수업에 현재 참여 중인지 확인
    public boolean isActiveMember(Long classId, Long studentId) {
        return memberRepository.existsByClassroom_IdAndUser_IdAndLeftAtIsNull(classId, studentId);
    }

    private ClassRoomEntity getClassroom(Long classId) {
        return classroomRepository.findById(classId)
                .orElseThrow(ClassNotFoundException::new);
    }

    private boolean isTeacherOf(ClassRoomEntity classroom, Long userId) {
        return classroom.getTeacher().getId().equals(userId);
    }
}
