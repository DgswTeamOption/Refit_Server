package com.option.refit.global.error.fileError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.fileError.FileErrorCode;

public class FileSizeLimitExceededException extends CustomException {
    public FileSizeLimitExceededException() {
        super(FileErrorCode.FILE_SIZE_EXCEEDED);
    }
}
