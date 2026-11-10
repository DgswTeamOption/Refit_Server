package com.option.refit.domain.notice.service.impl;

import com.option.refit.domain.classroom.entity.ClassRoomEntity;
import com.option.refit.domain.classroom.repository.ClassRoomRepository;
import com.option.refit.domain.notice.dto.NoticeRequest;
import com.option.refit.domain.notice.entity.NoticeEntity;
import com.option.refit.domain.notice.repository.NoticeRepository;
import com.option.refit.domain.notice.service.NoticeCreateService;
import com.option.refit.domain.user.entity.Role;
import com.option.refit.global.error.classError.exception.ClassNotFoundException;
import com.option.refit.global.error.noticeError.exception.NoticePermissionDeniedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NoticeCreateServiceImpl implements NoticeCreateService {

    private final NoticeRepository noticeRepository;
    private final ClassRoomRepository classRoomRepository;

    @Override
    @Transactional
    public Long createNotice(
            Long classId,
            Long userId,
            Role role,
            NoticeRequest.Create request
    ) {
        ClassRoomEntity classroom = classRoomRepository.findById(classId)
                .orElseThrow(ClassNotFoundException::new);

        boolean isResponsibleTeacher = role == Role.TEACHER
                && classroom.getTeacher().getId().equals(userId);

        if (!isResponsibleTeacher) {
            throw new NoticePermissionDeniedException();
        }

        NoticeEntity notice = NoticeEntity.builder()
                .classId(classId)
                .writerId(userId)
                .title(request.getTitle())
                .content(request.getContent())
                .build();

        return noticeRepository.save(notice).getId();
    }
}