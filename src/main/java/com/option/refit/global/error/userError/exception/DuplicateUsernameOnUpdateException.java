package com.option.refit.global.error.userError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.userError.UserErrorCode;

public class DuplicateUsernameOnUpdateException extends CustomException {
    public DuplicateUsernameOnUpdateException() {
        super(UserErrorCode.DUPLICATE_USERNAME_ON_UPDATE);
    }
}
