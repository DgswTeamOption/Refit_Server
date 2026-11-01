package com.option.refit.global.error.authError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.authError.AuthErrorCode;

public class InactiveUserException extends CustomException {
    public InactiveUserException() {
        super(AuthErrorCode.INACTIVE_USER);
    }
}
