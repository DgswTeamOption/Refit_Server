package com.option.refit.global.error.calendarError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.calendarError.CalendarErrorCode;

public class CalendarMemoAccessDeniedException extends CustomException {
    public CalendarMemoAccessDeniedException() {
        super(CalendarErrorCode.CALENDAR_MEMO_ACCESS_DENIED);
    }
}
