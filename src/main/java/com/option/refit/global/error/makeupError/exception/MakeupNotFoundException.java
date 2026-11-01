package com.option.refit.global.error.makeupError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.makeupError.MakeupErrorCode;

public class MakeupNotFoundException extends CustomException {
    public MakeupNotFoundException() {
        super(MakeupErrorCode.MAKEUP_NOT_FOUND);
    }
}
