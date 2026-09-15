package com.option.refit.global.error.notification.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.notification.NotificationErrorCode;

public class NotificationAccessDeniedException extends CustomException {
    public NotificationAccessDeniedException() {
        super(NotificationErrorCode.NOTIFICATION_ACCESS_DENIED);
    }
}
