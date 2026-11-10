package com.option.refit.domain.notice.service.impl;

import com.option.refit.domain.classroom.entity.ClassRoomEntity;
import com.option.refit.domain.classroom.repository.ClassRoomMemberRepository;
import com.option.refit.domain.classroom.repository.ClassRoomRepository;
import com.option.refit.domain.notice.dto.NoticeResponse;
import com.option.refit.domain.notice.repository.NoticeRepository;
import com.option.refit.domain.notice.service.NoticeGetService;
import com.option.refit.domain.user.entity.Role;
import com.option.refit.global.error.classError.exception.ClassAccessDeniedException;
import com.option.refit.global.error.classError.exception.ClassNotFoundException;
import com.option.refit.global.error.noticeError.exception.NoticeNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeGetServiceImpl implements NoticeGetService {

    private final NoticeRepository noticeRepository;
    private final ClassRoomRepository classRoomRepository;
    private final ClassRoomMemberRepository classRoomMemberRepository;

    @Override
    public List<NoticeResponse> getNotices(
            Long classId,
            Long userId,
            Role role
    ) {
        validateClassAccess(classId, userId, role);

        return noticeRepository.findByClassIdOrderByCreatedAtDesc(classId)
                .stream()
                .map(NoticeResponse::new)
                .toList();
    }

    @Override
    public NoticeResponse getNotice(
            Long classId,
            Long noticeId,
            Long userId,
            Role role
    ) {
        validateClassAccess(classId, userId, role);

        return noticeRepository.findByIdAndClassId(noticeId, classId)
                .map(NoticeResponse::new)
                .orElseThrow(NoticeNotFoundException::new);
    }

    private void validateClassAccess(
            Long classId,
            Long userId,
            Role role
    ) {
        ClassRoomEntity classroom = classRoomRepository.findById(classId)
                .orElseThrow(ClassNotFoundException::new);

        boolean isTeacher = role == Role.TEACHER
                && classroom.getTeacher().getId().equals(userId);

        boolean isActiveStudent = role == Role.STUDENT
                && classRoomMemberRepository
                .existsByClassroom_IdAndUser_IdAndLeftAtIsNull(
                        classId,
                        userId
                );

        if (!isTeacher && !isActiveStudent) {
            throw new ClassAccessDeniedException();
        }
    }
}