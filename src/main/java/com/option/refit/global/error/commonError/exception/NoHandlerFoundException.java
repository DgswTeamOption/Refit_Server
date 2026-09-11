package com.option.refit.global.error.commonError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.commonError.CommonErrorCode;

public class NoHandlerFoundException extends CustomException {
    public NoHandlerFoundException() {
        super(CommonErrorCode.NO_HANDLER_FOUND);
    }
}
