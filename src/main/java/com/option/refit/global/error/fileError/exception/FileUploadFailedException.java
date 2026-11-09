package com.option.refit.global.error.fileError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.fileError.FileErrorCode;

public class FileUploadFailedException extends CustomException {
    public FileUploadFailedException(Throwable cause) {
        super(FileErrorCode.FILE_UPLOAD_FAILED, cause);
    }
}
