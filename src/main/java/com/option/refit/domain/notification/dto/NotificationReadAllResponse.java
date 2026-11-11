package com.option.refit.domain.notification.dto;

// 알림 모두 읽음 결과: 이번 요청으로 읽음 처리된 알림 개수 (이미 다 읽었으면 0)
public record NotificationReadAllResponse(int updatedCount) {
}
