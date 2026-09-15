package com.option.refit.global.error.jwtError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.jwtError.JwtErrorCode;

public class ExpiredTokenException extends CustomException {
    public ExpiredTokenException() {
        super(JwtErrorCode.EXPIRED_TOKEN);
    }
}
