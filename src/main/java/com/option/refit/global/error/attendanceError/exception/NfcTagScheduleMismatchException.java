package com.option.refit.global.error.attendanceError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.attendanceError.AttendanceErrorCode;

public class NfcTagScheduleMismatchException extends CustomException {
    public NfcTagScheduleMismatchException() {
        super(AttendanceErrorCode.NFC_TAG_SCHEDULE_MISMATCH);
    }
}
