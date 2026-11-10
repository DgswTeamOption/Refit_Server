package com.option.refit.global.error.makeupError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.makeupError.MakeupErrorCode;

public class InvalidApprovalStatusException extends CustomException {
    public InvalidApprovalStatusException() {
        super(MakeupErrorCode.INVALID_APPROVAL_STATUS);
    }
}
