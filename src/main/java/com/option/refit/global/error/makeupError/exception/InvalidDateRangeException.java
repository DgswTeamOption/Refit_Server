package com.option.refit.global.error.makeupError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.makeupError.MakeupErrorCode;

public class InvalidDateRangeException extends CustomException {
    public InvalidDateRangeException() {
        super(MakeupErrorCode.INVALID_DATE_RANGE);
    }
}
