package com.option.refit.global.error.classError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.classError.ClazzErrorCode;

public class ClassNotFoundException extends CustomException {
    public ClassNotFoundException() {
        super(ClazzErrorCode.CLASS_NOT_FOUND);
    }
}
