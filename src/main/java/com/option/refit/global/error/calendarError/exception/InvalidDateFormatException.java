package com.option.refit.global.error.calendarError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.calendarError.CalendarErrorCode;

public class InvalidDateFormatException extends CustomException {
    public InvalidDateFormatException() {
        super(CalendarErrorCode.INVALID_DATE_FORMAT);
    }
}
