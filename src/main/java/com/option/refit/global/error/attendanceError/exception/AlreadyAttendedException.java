package com.option.refit.global.error.attendanceError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.attendanceError.AttendanceErrorCode;

public class AlreadyAttendedException extends CustomException {
    public AlreadyAttendedException() {
        super(AttendanceErrorCode.ALREADY_ATTENDED);
    }
}
