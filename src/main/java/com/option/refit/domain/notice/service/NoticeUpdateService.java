package com.option.refit.domain.notice.service;

import com.option.refit.domain.notice.dto.NoticeRequest;

public interface NoticeUpdateService {
    void updateNotice(Long classId, Long noticeId, Long userId, NoticeRequest.CreateOrUpdate request);
}
