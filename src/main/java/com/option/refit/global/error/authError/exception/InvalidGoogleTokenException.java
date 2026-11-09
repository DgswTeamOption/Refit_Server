package com.option.refit.global.error.authError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.authError.AuthErrorCode;

public class InvalidGoogleTokenException extends CustomException {
    public InvalidGoogleTokenException() {
        super(AuthErrorCode.INVALID_GOOGLE_TOKEN);
    }

    public InvalidGoogleTokenException(Throwable cause) {
        super(AuthErrorCode.INVALID_GOOGLE_TOKEN, cause);
    }
}
