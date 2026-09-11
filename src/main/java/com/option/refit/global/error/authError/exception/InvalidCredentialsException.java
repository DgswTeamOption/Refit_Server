package com.option.refit.global.error.authError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.authError.AuthErrorCode;

public class InvalidCredentialsException extends CustomException {
    public InvalidCredentialsException() {
        super(AuthErrorCode.INVALID_CREDENTIALS);
    }
}
