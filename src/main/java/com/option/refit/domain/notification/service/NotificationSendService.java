package com.option.refit.domain.notification.service;

// 알림 발송 (알림 행 저장). 호출한 쪽의 트랜잭션에 참여하므로 본 작업이 롤백되면 알림도 함께 롤백된다.
public interface NotificationSendService {

    void send(Long teacherId, Long studentId, Long recipientId, String message, String notificationType);
}
