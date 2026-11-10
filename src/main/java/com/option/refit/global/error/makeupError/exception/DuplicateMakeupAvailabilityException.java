package com.option.refit.global.error.makeupError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.makeupError.MakeupErrorCode;

public class DuplicateMakeupAvailabilityException extends CustomException {
    public DuplicateMakeupAvailabilityException() {
        super(MakeupErrorCode.DUPLICATE_MAKEUP_AVAILABILITY);
    }
}
