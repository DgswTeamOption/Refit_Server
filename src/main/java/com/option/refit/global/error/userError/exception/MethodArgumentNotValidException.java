package com.option.refit.global.error.userError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.userError.UserErrorCode;

public class MethodArgumentNotValidException extends CustomException {
    public MethodArgumentNotValidException() {
        super(UserErrorCode.METHOD_ARGUMENT_NOT_VALID);
    }
}
