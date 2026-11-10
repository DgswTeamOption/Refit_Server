package com.option.refit.global.error.fileError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.fileError.FileErrorCode;

public class EmptyFileException extends CustomException {
    public EmptyFileException() {
        super(FileErrorCode.EMPTY_FILE);
    }
}
