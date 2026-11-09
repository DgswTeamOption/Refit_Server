package com.option.refit.domain.notification.service.impl;

import com.option.refit.domain.notification.entity.NotificationEntity;
import com.option.refit.domain.notification.repository.NotificationRepository;
import com.option.refit.domain.notification.service.NotificationDeleteService;
import com.option.refit.global.error.notification.exception.NotificationNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationDeleteServiceImpl implements NotificationDeleteService {

    private final NotificationRepository notificationRepository;

    @Override
    public void deleteNotification(Long notificationId, Long userId) {
        NotificationEntity notification = notificationRepository
                .findByIdAndRecipientId(notificationId, userId)
                .orElseThrow(NotificationNotFoundException::new);

        notificationRepository.delete(notification);
    }
}
