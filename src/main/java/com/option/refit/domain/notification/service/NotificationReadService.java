package com.option.refit.domain.notification.service;

import com.option.refit.domain.notification.dto.NotificationReadAllResponse;
import com.option.refit.domain.notification.dto.NotificationResponse;

public interface NotificationReadService {

    // 알림 한 건을 읽음 처리한다. 알림이 없으면 404, 본인 알림이 아니면 403
    NotificationResponse readNotification(Long notificationId, Long userId);

    // 본인의 안 읽은 알림을 모두 읽음 처리하고 변경된 개수를 반환한다.
    NotificationReadAllResponse readAllNotifications(Long userId);
}
