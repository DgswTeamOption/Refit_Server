package com.option.refit.global.error.classError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.classError.ClazzErrorCode;

public class ClassPermissionDeniedException extends CustomException {
    public ClassPermissionDeniedException() {
        super(ClazzErrorCode.CLASS_PERMISSION_DENIED);
    }
}
