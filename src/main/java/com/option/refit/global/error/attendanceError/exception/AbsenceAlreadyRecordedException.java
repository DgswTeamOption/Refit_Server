package com.option.refit.global.error.attendanceError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.attendanceError.AttendanceErrorCode;

public class AbsenceAlreadyRecordedException extends CustomException {
    public AbsenceAlreadyRecordedException() {
        super(AttendanceErrorCode.ABSENCE_ALREADY_RECORDED);
    }
}
