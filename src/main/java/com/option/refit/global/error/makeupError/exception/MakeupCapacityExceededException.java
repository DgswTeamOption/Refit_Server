package com.option.refit.global.error.makeupError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.makeupError.MakeupErrorCode;

public class MakeupCapacityExceededException extends CustomException {
    public MakeupCapacityExceededException() {
        super(MakeupErrorCode.MAKEUP_CAPACITY_EXCEEDED);
    }
}
