package com.option.refit.global.error.calendarError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.calendarError.CalendarErrorCode;

public class ScheduleNotFoundException extends CustomException {
    public ScheduleNotFoundException() {
        super(CalendarErrorCode.SCHEDULE_NOT_FOUND);
    }
}
