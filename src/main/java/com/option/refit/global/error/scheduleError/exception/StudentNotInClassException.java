package com.option.refit.global.error.scheduleError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.scheduleError.ScheduleErrorCode;

public class StudentNotInClassException extends CustomException {
    public StudentNotInClassException() {
        super(ScheduleErrorCode.STUDENT_NOT_IN_CLASS);
    }
}
