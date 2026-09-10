package com.option.refit.global.error.userError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.userError.UserErrorCode;

public class FileSizeExceededException extends CustomException {
    public FileSizeExceededException() {
        super(UserErrorCode.FILE_SIZE_EXCEEDED);
    }
}
