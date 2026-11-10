package com.option.refit.global.error.absenceError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.absenceError.AbsenceErrorCode;

public class AbsenceNotFoundException extends CustomException {
    public AbsenceNotFoundException() {
        super(AbsenceErrorCode.ABSENCE_NOT_FOUND);
    }
}
