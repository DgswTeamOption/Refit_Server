package com.option.refit.global.error.makeupError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.makeupError.MakeupErrorCode;

public class MakeupAvailabilityNotFoundException extends CustomException {
    public MakeupAvailabilityNotFoundException() {
        super(MakeupErrorCode.MAKEUP_AVAILABILITY_NOT_FOUND);
    }
}
