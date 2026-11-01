package com.option.refit.global.error.securityError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.securityError.SecurityErrorCode;

public class RoleAccessDeniedException extends CustomException {
    public RoleAccessDeniedException() {
        super(SecurityErrorCode.ROLE_ACCESS_DENIED);
    }
}
