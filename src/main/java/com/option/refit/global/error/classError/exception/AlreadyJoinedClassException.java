package com.option.refit.global.error.classError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.classError.ClazzErrorCode;

public class AlreadyJoinedClassException extends CustomException {
    public AlreadyJoinedClassException() {
        super(ClazzErrorCode.ALREADY_JOINED_CLASS);
    }
}
