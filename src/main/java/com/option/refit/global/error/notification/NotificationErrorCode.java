package com.option.refit.global.error.notification;

import com.option.refit.global.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum NotificationErrorCode implements ErrorCode {
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 알림입니다"),
    NOTIFICATION_ACCESS_DENIED(HttpStatus.FORBIDDEN,"해당 알림에 접근할 권한이 없습니다");

    private final HttpStatus httpStatus;
    private final String message;
}