package com.option.refit.global.error.authError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.authError.AuthErrorCode;

public class EmailSendFailedException extends CustomException {
    public EmailSendFailedException(Throwable cause) {
        super(AuthErrorCode.EMAIL_SEND_FAILED, cause);
    }
}
