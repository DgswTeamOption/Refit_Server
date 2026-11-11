package com.option.refit.domain.notification.service.impl;

import com.option.refit.domain.notification.dto.NotificationReadAllResponse;
import com.option.refit.domain.notification.dto.NotificationResponse;
import com.option.refit.domain.notification.entity.NotificationEntity;
import com.option.refit.domain.notification.repository.NotificationRepository;
import com.option.refit.domain.notification.service.NotificationReadService;
import com.option.refit.global.error.notification.exception.NotificationAccessDeniedException;
import com.option.refit.global.error.notification.exception.NotificationNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationReadServiceImpl implements NotificationReadService {

    private final NotificationRepository notificationRepository;

    @Override
    public NotificationResponse readNotification(Long notificationId, Long userId) {
        // 삭제된 알림은 @SQLRestriction 때문에 조회되지 않아 404가 된다.
        NotificationEntity notification = notificationRepository.findById(notificationId)
                .orElseThrow(NotificationNotFoundException::new);

        // 수신자 본인만 읽음 처리할 수 있다.
        if (!notification.getRecipientId().equals(userId)) {
            throw new NotificationAccessDeniedException();
        }

        notification.markAsRead();
        return new NotificationResponse(notification);
    }

    @Override
    public NotificationReadAllResponse readAllNotifications(Long userId) {
        return new NotificationReadAllResponse(
                notificationRepository.markAllAsRead(userId, LocalDateTime.now()));
    }
}
