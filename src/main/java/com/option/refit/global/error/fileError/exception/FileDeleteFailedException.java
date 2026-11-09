package com.option.refit.global.error.fileError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.fileError.FileErrorCode;

public class FileDeleteFailedException extends CustomException {
    public FileDeleteFailedException(Throwable cause) {
        super(FileErrorCode.FILE_DELETE_FAILED, cause);
    }
}
