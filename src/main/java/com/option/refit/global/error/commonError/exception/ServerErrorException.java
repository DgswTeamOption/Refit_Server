package com.option.refit.global.error.commonError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.commonError.CommonErrorCode;

public class ServerErrorException extends CustomException {
    public ServerErrorException() {
        super(CommonErrorCode.SERVER_ERROR);
    }
}
