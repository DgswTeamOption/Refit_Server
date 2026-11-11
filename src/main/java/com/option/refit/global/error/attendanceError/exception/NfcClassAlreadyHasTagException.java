package com.option.refit.global.error.attendanceError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.attendanceError.AttendanceErrorCode;

public class NfcClassAlreadyHasTagException extends CustomException {
    public NfcClassAlreadyHasTagException() {
        super(AttendanceErrorCode.NFC_CLASS_ALREADY_HAS_TAG);
    }
}
