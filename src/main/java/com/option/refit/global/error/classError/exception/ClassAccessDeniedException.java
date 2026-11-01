package com.option.refit.global.error.classError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.classError.ClazzErrorCode;

public class ClassAccessDeniedException extends CustomException {
    public ClassAccessDeniedException() {
        super(ClazzErrorCode.CLASS_ACCESS_DENIED);
    }
}
