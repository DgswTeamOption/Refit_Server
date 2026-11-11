package com.option.refit.global.error.makeupError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.makeupError.MakeupErrorCode;

public class MakeupAvailabilityAccessDeniedException extends CustomException {
    public MakeupAvailabilityAccessDeniedException() {
        super(MakeupErrorCode.MAKEUP_AVAILABILITY_ACCESS_DENIED);
    }
}
