package com.option.refit.global.error.noticeError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.noticeError.NoticeErrorCode;

public class NoticeAccessDeniedException extends CustomException {
    public NoticeAccessDeniedException() {
        super(NoticeErrorCode.NOTICE_ACCESS_DENIED);
    }
}
