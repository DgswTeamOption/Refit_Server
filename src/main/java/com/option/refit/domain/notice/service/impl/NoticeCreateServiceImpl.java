package com.option.refit.domain.notice.service.impl;

import com.option.refit.domain.notice.dto.NoticeRequest;
import com.option.refit.domain.notice.entity.NoticeEntity;
import com.option.refit.domain.notice.repository.NoticeRepository;
import com.option.refit.domain.notice.service.NoticeCreateService;
import com.option.refit.domain.user.entity.Role;
import com.option.refit.global.error.noticeError.exception.NoticePermissionDeniedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NoticeCreateServiceImpl implements NoticeCreateService {

    private final NoticeRepository noticeRepository;

    @Override
    @Transactional
    public Long createNotice(
            Long classId,
            Long userId,
            Role role,
            NoticeRequest.Create request
    ) {
        // TODO: Classroom 기능과 연결해 이 반의 담당 교사인지 확인
        if (role != Role.TEACHER) {
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