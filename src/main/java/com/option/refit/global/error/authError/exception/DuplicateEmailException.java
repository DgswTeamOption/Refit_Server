package com.option.refit.global.error.authError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.authError.AuthErrorCode;

public class DuplicateEmailException extends CustomException {
    public DuplicateEmailException() {
        super(AuthErrorCode.DUPLICATE_EMAIL);
    }
}
