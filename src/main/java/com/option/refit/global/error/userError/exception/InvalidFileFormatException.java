package com.option.refit.global.error.userError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.userError.UserErrorCode;

public class InvalidFileFormatException extends CustomException {
    public InvalidFileFormatException() {
        super(UserErrorCode.INVALID_FILE_FORMAT);
    }
}
