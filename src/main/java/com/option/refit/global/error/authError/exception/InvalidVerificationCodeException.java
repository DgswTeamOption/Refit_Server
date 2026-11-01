package com.option.refit.global.error.authError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.authError.AuthErrorCode;

public class InvalidVerificationCodeException extends CustomException {
    public InvalidVerificationCodeException() {
        super(AuthErrorCode.INVALID_VERIFICATION_CODE);
    }
}
