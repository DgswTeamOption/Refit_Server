package com.option.refit.global.error.makeupError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.makeupError.MakeupErrorCode;

public class MakeupCapacityBelowAppliedException extends CustomException {
    public MakeupCapacityBelowAppliedException() {
        super(MakeupErrorCode.MAKEUP_CAPACITY_BELOW_APPLIED);
    }
}
