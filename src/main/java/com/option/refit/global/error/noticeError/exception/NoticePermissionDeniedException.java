package com.option.refit.global.error.noticeError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.noticeError.NoticeErrorCode;

public class NoticePermissionDeniedException extends CustomException {
    public NoticePermissionDeniedException() {
        super(NoticeErrorCode.NOTICE_PERMISSION_DENIED);
    }
}
