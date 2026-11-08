package com.option.refit.domain.classroom.service.impl;

import com.option.refit.domain.classroom.dto.response.ClassRoomHomeResponse;
import com.option.refit.domain.classroom.entity.ClassRoomEntity;
import com.option.refit.domain.classroom.repository.ClassRoomMemberRepository;
import com.option.refit.domain.classroom.repository.ClassRoomRepository;
import com.option.refit.domain.classroom.service.ClassRoomHomeService;
import com.option.refit.domain.user.entity.User;
import com.option.refit.global.error.classError.exception.ClassAccessDeniedException;
import com.option.refit.global.error.classError.exception.ClassNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClassRoomHomeServiceImpl implements ClassRoomHomeService {

    private final ClassRoomRepository classroomRepository;
    private final ClassRoomMemberRepository memberRepository;

    @Override
    @Transactional(readOnly = true)
    public ClassRoomHomeResponse getClassroomHome(
            Long classroomId,
            User user
    ) {
        ClassRoomEntity classroom = classroomRepository
                .findById(classroomId)
                .orElseThrow(ClassNotFoundException::new);

        boolean isTeacher = classroom.getTeacher()
                .getId()
                .equals(user.getId());

        boolean isMember = memberRepository
                .existsByClassroom_IdAndUser_IdAndLeftAtIsNull(
                        classroomId,
                        user.getId()
                );

        if (!isTeacher && !isMember) {
            throw new ClassAccessDeniedException();
        }

        long memberCount = memberRepository
                .countByClassroom_IdAndLeftAtIsNull(classroomId);

        return ClassRoomHomeResponse.from(classroom, memberCount);
    }
}
