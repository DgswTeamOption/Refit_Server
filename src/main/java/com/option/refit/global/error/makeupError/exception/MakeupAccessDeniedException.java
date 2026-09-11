package com.option.refit.global.error.makeupError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.makeupError.MakeupErrorCode;

public class MakeupAccessDeniedException extends CustomException {
    public MakeupAccessDeniedException() {
        super(MakeupErrorCode.MAKEUP_ACCESS_DENIED);
    }
}
