package com.option.refit.domain.notification.service;

import com.option.refit.domain.notification.dto.NotificationResponse;
import com.option.refit.domain.user.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationGetService {
    Page<NotificationResponse> getNotifications(Long userId, Role role, Pageable pageable);
}