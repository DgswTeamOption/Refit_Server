package com.option.refit.global.error.userError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.userError.UserErrorCode;

public class UserAccessDeniedException extends CustomException {
    public UserAccessDeniedException() {
        super(UserErrorCode.USER_ACCESS_DENIED);
    }
}
