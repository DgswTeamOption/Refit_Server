package com.option.refit.domain.notice.service;

import com.option.refit.domain.notice.dto.NoticeResponse;

import java.util.List;

public interface NoticeGetService {
    List<NoticeResponse> getNotices(Long classId);

    NoticeResponse getNotice(Long classId, Long noticeId);
}
