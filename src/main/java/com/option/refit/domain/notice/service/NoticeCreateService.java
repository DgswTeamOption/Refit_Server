package com.option.refit.domain.notice.service;

import com.option.refit.domain.notice.dto.NoticeRequest;
import com.option.refit.domain.user.entity.Role;

public interface NoticeCreateService {
    Long createNotice(Long classId, Long userId, Role role, NoticeRequest.CreateOrUpdate request);
}
