package com.option.refit.global.error.attendanceError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.attendanceError.AttendanceErrorCode;

public class OutsideAttendanceTimeException extends CustomException {
    public OutsideAttendanceTimeException() {
        super(AttendanceErrorCode.OUTSIDE_ATTENDANCE_TIME);
    }
}
