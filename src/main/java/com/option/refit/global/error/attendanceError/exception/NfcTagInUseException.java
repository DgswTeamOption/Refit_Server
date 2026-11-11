package com.option.refit.global.error.attendanceError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.attendanceError.AttendanceErrorCode;

public class NfcTagInUseException extends CustomException {
    public NfcTagInUseException() {
        super(AttendanceErrorCode.NFC_TAG_IN_USE);
    }
}
