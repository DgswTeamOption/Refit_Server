package com.option.refit.global.error.fileError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.fileError.FileErrorCode;

public class FileAccessDeniedException extends CustomException {
    public FileAccessDeniedException() {
        super(FileErrorCode.FILE_ACCESS_DENIED);
    }
}
