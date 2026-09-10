package com.option.refit.global.error.classError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.classError.ClazzErrorCode;

public class MethodArgumentNotValidException extends CustomException {
    public MethodArgumentNotValidException() {
        super(ClazzErrorCode.METHOD_ARGUMENT_NOT_VALID);
    }
}
