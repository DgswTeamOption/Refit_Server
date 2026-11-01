package com.option.refit.global.error.absenceError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.absenceError.AbsenceErrorCode;

public class InvalidDateRangeException extends CustomException {
    public InvalidDateRangeException() {
        super(AbsenceErrorCode.INVALID_DATE_RANGE);
    }
}
