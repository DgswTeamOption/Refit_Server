package com.option.refit.global.error.statsError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.statsError.StatsErrorCode;

public class AccessDeniedException extends CustomException {
    public AccessDeniedException() {
        super(StatsErrorCode.ACCESS_DENIED);
    }
}
