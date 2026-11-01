package com.option.refit.global.error.absenceError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.absenceError.AbsenceErrorCode;

public class MethodArgumentNotValidException extends CustomException {
    public MethodArgumentNotValidException() {
        super(AbsenceErrorCode.METHOD_ARGUMENT_NOT_VALID);
    }
}
