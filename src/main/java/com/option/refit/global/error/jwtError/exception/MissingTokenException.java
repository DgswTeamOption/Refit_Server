package com.option.refit.global.error.jwtError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.jwtError.JwtErrorCode;

public class MissingTokenException extends CustomException {
    public MissingTokenException() {
        super(JwtErrorCode.MISSING_TOKEN);
    }
}
