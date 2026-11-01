package com.option.refit.global.error.jwtError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.jwtError.JwtErrorCode;

public class InvalidTokenSignatureException extends CustomException {
    public InvalidTokenSignatureException() {
        super(JwtErrorCode.INVALID_TOKEN_SIGNATURE);
    }
}
