package com.option.refit.global.error.classError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.classError.ClazzErrorCode;

public class InvalidClassCodeException extends CustomException {
    public InvalidClassCodeException() {
        super(ClazzErrorCode.INVALID_CLASS_CODE);
    }
}
