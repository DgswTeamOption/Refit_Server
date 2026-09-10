package com.option.refit.global.error.makeupError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.makeupError.MakeupErrorCode;

public class AccessDeniedException extends CustomException {
    public AccessDeniedException() {
        super(MakeupErrorCode.ACCESS_DENIED);
    }
}
