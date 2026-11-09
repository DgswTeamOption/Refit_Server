package com.option.refit.global.error.fileError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.fileError.FileErrorCode;

public class InvalidFilePathException extends CustomException {
    public InvalidFilePathException() {
        super(FileErrorCode.INVALID_FILE_PATH);
    }
}
