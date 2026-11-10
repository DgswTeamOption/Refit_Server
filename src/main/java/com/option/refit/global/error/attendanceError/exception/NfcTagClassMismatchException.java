package com.option.refit.global.error.attendanceError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.attendanceError.AttendanceErrorCode;

public class NfcTagClassMismatchException extends CustomException {
    public NfcTagClassMismatchException() {
        super(AttendanceErrorCode.NFC_TAG_CLASS_MISMATCH);
    }
}
