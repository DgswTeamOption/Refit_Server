package com.option.refit.global.error.jwtError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.jwtError.JwtErrorCode;

public class MalformedTokenException extends CustomException {
    public MalformedTokenException() {
        super(JwtErrorCode.MALFORMED_TOKEN);
    }
}
