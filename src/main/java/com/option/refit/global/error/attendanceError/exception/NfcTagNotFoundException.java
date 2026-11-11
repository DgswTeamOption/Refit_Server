package com.option.refit.global.error.attendanceError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.attendanceError.AttendanceErrorCode;

public class NfcTagNotFoundException extends CustomException {
    public NfcTagNotFoundException() {
        super(AttendanceErrorCode.NFC_TAG_NOT_FOUND);
    }
}
