package com.option.refit.global.error.calendarError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.calendarError.CalendarErrorCode;

public class CalendarMemoNotFoundException extends CustomException {
    public CalendarMemoNotFoundException() {
        super(CalendarErrorCode.CALENDAR_MEMO_NOT_FOUND);
    }
}
