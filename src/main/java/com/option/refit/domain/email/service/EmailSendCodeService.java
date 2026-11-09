package com.option.refit.domain.email.service;

import com.option.refit.domain.email.dto.request.EmailSendCodeRequest;
import com.option.refit.domain.email.dto.response.EmailVerificationResponse;

public interface EmailSendCodeService {
    EmailVerificationResponse sendCode(EmailSendCodeRequest request);
}
