package com.option.refit.domain.notice.service;

import com.option.refit.domain.notice.dto.NoticeResponse;
import com.option.refit.domain.user.entity.Role;

import java.util.List;

public interface NoticeGetService {

    List<NoticeResponse> getNotices(Long classId, Long userId, Role role);

    NoticeResponse getNotice(
            Long classId,
            Long noticeId,
            Long userId,
            Role role
    );
}