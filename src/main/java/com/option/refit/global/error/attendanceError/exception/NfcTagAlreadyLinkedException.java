package com.option.refit.global.error.attendanceError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.attendanceError.AttendanceErrorCode;

public class NfcTagAlreadyLinkedException extends CustomException {
    public NfcTagAlreadyLinkedException() {
        super(AttendanceErrorCode.NFC_TAG_ALREADY_LINKED);
    }
}
