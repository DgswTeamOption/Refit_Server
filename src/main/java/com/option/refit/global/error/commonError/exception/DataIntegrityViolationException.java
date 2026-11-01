package com.option.refit.global.error.commonError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.commonError.CommonErrorCode;
import jakarta.persistence.Column;

public class DataIntegrityViolationException extends CustomException {
    public DataIntegrityViolationException() {
        super(CommonErrorCode.DATA_INTEGRITY_VIOLATION);
    }
}
