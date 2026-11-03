package com.option.refit.domain.email.service;

import com.option.refit.domain.email.dto.request.EmailVerifyCodeRequest;
import com.option.refit.domain.email.dto.response.EmailVerificationResponse;

public interface EmailVerifyCodeService {
    EmailVerificationResponse verifyCode(EmailVerifyCodeRequest request);
}
