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
    public Long createNotice(Long classId, Long userId, Role role, NoticeRequest.CreateOrUpdate request) {
        // TODO: classRoom 머지 후 "이 반 담당 선생님인지" 검사로 교체
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
