package com.option.refit.global.error.makeupError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.makeupError.MakeupErrorCode;

public class DuplicateMakeupException extends CustomException {
    public DuplicateMakeupException() {
        super(MakeupErrorCode.DUPLICATE_MAKEUP);
    }
}
