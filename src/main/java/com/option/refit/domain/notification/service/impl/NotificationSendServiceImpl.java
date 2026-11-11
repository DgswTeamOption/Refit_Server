package com.option.refit.domain.notification.service.impl;

import com.option.refit.domain.notification.entity.NotificationEntity;
import com.option.refit.domain.notification.repository.NotificationRepository;
import com.option.refit.domain.notification.service.NotificationSendService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationSendServiceImpl implements NotificationSendService {

    private final NotificationRepository notificationRepository;

    @Override
    @Transactional
    public void send(Long teacherId, Long studentId, Long recipientId, String message, String notificationType) {
        notificationRepository.save(NotificationEntity.builder()
                .teacherId(teacherId)
                .studentId(studentId)
                .recipientId(recipientId)
                .message(message)
                .notificationType(notificationType)
                .build());
    }
}
