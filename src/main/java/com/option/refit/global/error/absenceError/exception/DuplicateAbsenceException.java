package com.option.refit.global.error.absenceError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.absenceError.AbsenceErrorCode;

public class DuplicateAbsenceException extends CustomException {
    public DuplicateAbsenceException() {
        super(AbsenceErrorCode.DUPLICATE_ABSENCE);
    }
}
