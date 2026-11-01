package com.option.refit.global.error.notification.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.notification.NotificationErrorCode;

public class NotificationNotFoundException extends CustomException {
    public NotificationNotFoundException() {
        super(NotificationErrorCode.NOTIFICATION_NOT_FOUND);
    }
}
