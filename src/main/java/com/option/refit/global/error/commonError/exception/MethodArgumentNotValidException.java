package com.option.refit.global.error.commonError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.commonError.CommonErrorCode;

public class MethodArgumentNotValidException extends CustomException {
    public MethodArgumentNotValidException() {
        super(CommonErrorCode.METHOD_ARGUMENT_NOT_VALID);
    }
}
