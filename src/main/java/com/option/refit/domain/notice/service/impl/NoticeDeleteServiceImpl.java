package com.option.refit.domain.notice.service.impl;

import com.option.refit.domain.notice.entity.NoticeEntity;
import com.option.refit.domain.notice.repository.NoticeRepository;
import com.option.refit.domain.notice.service.NoticeDeleteService;
import com.option.refit.global.error.noticeError.exception.NoticeAccessDeniedException;
import com.option.refit.global.error.noticeError.exception.NoticeNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NoticeDeleteServiceImpl implements NoticeDeleteService {

    private final NoticeRepository noticeRepository;

    @Override
    @Transactional
    public void deleteNotice(Long classId, Long noticeId, Long userId) {
        NoticeEntity notice = noticeRepository.findByIdAndClassId(noticeId, classId)
                .orElseThrow(NoticeNotFoundException::new);

        if (!notice.isWriter(userId)) {
            throw new NoticeAccessDeniedException();
        }

        noticeRepository.delete(notice);
    }
}
