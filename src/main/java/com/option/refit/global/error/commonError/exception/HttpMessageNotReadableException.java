package com.option.refit.global.error.commonError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.commonError.CommonErrorCode;

public class HttpMessageNotReadableException extends CustomException {
    public HttpMessageNotReadableException() {
        super(CommonErrorCode.HTTP_MESSAGE_NOT_READABLE);
    }
}
