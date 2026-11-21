package com.option.refit.global.error.scheduleError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.scheduleError.ScheduleErrorCode;

public class ScheduleOverlapException extends CustomException {
    public ScheduleOverlapException() {
        super(ScheduleErrorCode.SCHEDULE_OVERLAP);
    }
}
