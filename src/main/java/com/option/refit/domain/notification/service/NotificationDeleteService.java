package com.option.refit.domain.notification.service;

import com.option.refit.domain.user.entity.Role;

public interface NotificationDeleteService {
    void deleteNotification(Long notificationId, Long userId, Role role);
}