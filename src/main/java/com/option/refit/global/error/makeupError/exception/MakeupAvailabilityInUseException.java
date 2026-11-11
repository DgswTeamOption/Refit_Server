package com.option.refit.global.error.makeupError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.makeupError.MakeupErrorCode;

public class MakeupAvailabilityInUseException extends CustomException {
    public MakeupAvailabilityInUseException() {
        super(MakeupErrorCode.MAKEUP_AVAILABILITY_IN_USE);
    }
}
