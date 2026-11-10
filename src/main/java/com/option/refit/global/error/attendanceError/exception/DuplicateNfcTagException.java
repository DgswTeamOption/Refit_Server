package com.option.refit.global.error.attendanceError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.attendanceError.AttendanceErrorCode;

public class DuplicateNfcTagException extends CustomException {
    public DuplicateNfcTagException() {
        super(AttendanceErrorCode.DUPLICATE_NFC_TAG);
    }
}
