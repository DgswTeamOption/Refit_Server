package com.option.refit.global.error.userError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.userError.UserErrorCode;

public class UserNotFoundException extends CustomException {
    public UserNotFoundException() {
        super(UserErrorCode.USER_NOT_FOUND);
    }
}
