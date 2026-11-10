package com.option.refit.global.error.noticeError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.noticeError.NoticeErrorCode;

public class NoticeNotFoundException extends CustomException {
    public NoticeNotFoundException() {
        super(NoticeErrorCode.NOTICE_NOT_FOUND);
    }
}
