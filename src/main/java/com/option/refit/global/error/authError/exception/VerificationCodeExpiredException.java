package com.option.refit.global.error.authError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.authError.AuthErrorCode;

public class VerificationCodeExpiredException extends CustomException {
    public VerificationCodeExpiredException() {
        super(AuthErrorCode.VERIFICATION_CODE_EXPIRED);
    }
}
