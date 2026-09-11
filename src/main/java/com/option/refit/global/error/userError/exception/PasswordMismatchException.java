package com.option.refit.global.error.userError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.userError.UserErrorCode;

public class PasswordMismatchException extends CustomException {
    public PasswordMismatchException() {
        super(UserErrorCode.PASSWORD_MISMATCH);
    }
}
