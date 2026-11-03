package com.option.refit.global.error.scheduleError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.scheduleError.ScheduleErrorCode;

public class InvalidDateFormatException extends CustomException {
    public InvalidDateFormatException() {
        super(ScheduleErrorCode.INVALID_DATE_FORMAT);
    }
}
