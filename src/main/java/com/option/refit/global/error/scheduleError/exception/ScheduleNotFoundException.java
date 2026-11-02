package com.option.refit.global.error.scheduleError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.scheduleError.ScheduleErrorCode;

public class ScheduleNotFoundException extends CustomException {
    public ScheduleNotFoundException() {
        super(ScheduleErrorCode.SCHEDULE_NOT_FOUND);
    }
}
