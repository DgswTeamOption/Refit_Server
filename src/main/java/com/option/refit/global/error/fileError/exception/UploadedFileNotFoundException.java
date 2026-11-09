package com.option.refit.global.error.fileError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.fileError.FileErrorCode;

public class UploadedFileNotFoundException extends CustomException {
    public UploadedFileNotFoundException() {
        super(FileErrorCode.FILE_NOT_FOUND);
    }
}
