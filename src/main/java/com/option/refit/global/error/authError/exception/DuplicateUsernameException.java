package com.option.refit.global.error.authError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.authError.AuthErrorCode;

public class DuplicateUsernameException extends CustomException {
    public DuplicateUsernameException() {
        super(AuthErrorCode.DUPLICATE_USERNAME);
    }
}
