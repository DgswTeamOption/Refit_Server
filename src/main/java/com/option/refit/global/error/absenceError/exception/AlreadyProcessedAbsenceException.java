package com.option.refit.global.error.absenceError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.absenceError.AbsenceErrorCode;

public class AlreadyProcessedAbsenceException extends CustomException {
    public AlreadyProcessedAbsenceException() {
        super(AbsenceErrorCode.ALREADY_PROCESSED_ABSENCE);
    }
}
