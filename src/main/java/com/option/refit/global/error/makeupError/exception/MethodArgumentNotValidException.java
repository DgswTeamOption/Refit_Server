package com.option.refit.global.error.makeupError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.makeupError.MakeupErrorCode;

public class MethodArgumentNotValidException extends CustomException {
    public MethodArgumentNotValidException() {
        super(MakeupErrorCode.METHOD_ARGUMENT_NOT_VALID);
    }
}
