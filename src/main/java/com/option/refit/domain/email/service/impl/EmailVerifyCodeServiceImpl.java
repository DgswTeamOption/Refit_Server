package com.option.refit.domain.email.service.impl;

import com.option.refit.domain.email.dto.request.EmailVerifyCodeRequest;
import com.option.refit.domain.email.dto.response.EmailVerificationResponse;
import com.option.refit.domain.email.service.EmailVerifyCodeService;
import com.option.refit.global.error.authError.exception.InvalidVerificationCodeException;
import com.option.refit.global.error.authError.exception.TooManyVerificationAttemptsException;
import com.option.refit.global.error.authError.exception.VerificationCodeExpiredException;
import com.option.refit.global.verification.VerificationCodeStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Service
@RequiredArgsConstructor
public class EmailVerifyCodeServiceImpl implements EmailVerifyCodeService {

    private static final long MAX_ATTEMPTS = 5;

    private final VerificationCodeStore codeStore;

    @Override
    public EmailVerificationResponse verifyCode(EmailVerifyCodeRequest request) {
        String email = request.emailId();

        String savedCode = codeStore.findCode(email)
                .orElseThrow(VerificationCodeExpiredException::new);

        if (!matches(savedCode, request.verificationCode())) {
            if (codeStore.increaseAttempts(email) >= MAX_ATTEMPTS) {
                codeStore.deleteCode(email);
                throw new TooManyVerificationAttemptsException();
            }
            throw new InvalidVerificationCodeException();
        }

        codeStore.deleteCode(email);
        codeStore.markVerified(email);

        return EmailVerificationResponse.of(true);
    }

    private boolean matches(String saved, String input) {
        return MessageDigest.isEqual(
                saved.getBytes(StandardCharsets.UTF_8),
                input.getBytes(StandardCharsets.UTF_8));
    }
}
