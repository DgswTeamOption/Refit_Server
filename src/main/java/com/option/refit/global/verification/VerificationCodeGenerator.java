package com.option.refit.global.verification;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

// 6자리 숫자 인증코드 생성 (앞자리 0 포함)
@Component
public class VerificationCodeGenerator {

    private static final int CODE_BOUND = 1_000_000;

    private final SecureRandom random = new SecureRandom();

    public String generate() {
        return String.format("%06d", random.nextInt(CODE_BOUND));
    }
}
