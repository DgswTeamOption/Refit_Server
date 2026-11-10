package com.option.refit.domain.notice.service.impl;

import com.option.refit.domain.notice.dto.NoticeRequest;
import com.option.refit.domain.notice.entity.NoticeEntity;
import com.option.refit.domain.notice.repository.NoticeRepository;
import com.option.refit.domain.notice.service.NoticeUpdateService;
import com.option.refit.global.error.noticeError.exception.NoticeAccessDeniedException;
import com.option.refit.global.error.noticeError.exception.NoticeNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NoticeUpdateServiceImpl implements NoticeUpdateService {

    private final NoticeRepository noticeRepository;

    @Override
    @Transactional
    public void updateNotice(
            Long classId,
            Long noticeId,
            Long userId,
            NoticeRequest.Update request
    ) {
        NoticeEntity notice = noticeRepository.findByIdAndClassId(noticeId, classId)
                .orElseThrow(NoticeNotFoundException::new);

        if (!notice.isWriter(userId)) {
            throw new NoticeAccessDeniedException();
        }

        notice.updateNotice(request.getTitle(), request.getContent());
    }
}