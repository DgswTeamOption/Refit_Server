package com.option.refit.global.error.authError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.authError.AuthErrorCode;

public class TooManyVerificationAttemptsException extends CustomException {
    public TooManyVerificationAttemptsException() {
        super(AuthErrorCode.TOO_MANY_VERIFICATION_ATTEMPTS);
    }
}
