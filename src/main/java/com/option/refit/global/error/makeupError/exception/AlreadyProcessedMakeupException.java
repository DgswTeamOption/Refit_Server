package com.option.refit.global.error.makeupError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.makeupError.MakeupErrorCode;

public class AlreadyProcessedMakeupException extends CustomException {
    public AlreadyProcessedMakeupException() {
        super(MakeupErrorCode.ALREADY_PROCESSED_MAKEUP);
    }
}
