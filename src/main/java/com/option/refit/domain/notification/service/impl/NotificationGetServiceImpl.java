package com.option.refit.domain.notification.service.impl;

import com.option.refit.domain.notification.dto.NotificationResponse;
import com.option.refit.domain.notification.entity.NotificationEntity;
import com.option.refit.domain.notification.repository.NotificationRepository;
import com.option.refit.domain.notification.service.NotificationGetService;
import com.option.refit.domain.user.entity.Role;
import com.option.refit.global.error.jwtError.exception.MissingTokenException;
import com.option.refit.global.error.notification.exception.NotificationAccessDeniedException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationGetServiceImpl implements NotificationGetService {

    private final NotificationRepository notificationRepository;

    @Override
    public Page<NotificationResponse> getNotifications(Long userId, Role role, Pageable pageable) {
        if (userId == null || role == null) {
            throw new MissingTokenException();
        }

        Page<NotificationEntity> notifications;

        if (Role.TEACHER.equals(role)) {
            notifications = notificationRepository.findByTeacherId(userId, pageable);
        } else if (Role.STUDENT.equals(role)) {
            notifications = notificationRepository.findByStudentId(userId, pageable);
        } else {
            throw new NotificationAccessDeniedException();
        }

        return notifications.map(NotificationResponse::new);
    }
}