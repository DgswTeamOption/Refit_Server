package com.option.refit.global.error.absenceError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.absenceError.AbsenceErrorCode;

public class AbsenceAccessDeniedException extends CustomException {
    public AbsenceAccessDeniedException() {
        super(AbsenceErrorCode.ABSENCE_ACCESS_DENIED);
    }
}
