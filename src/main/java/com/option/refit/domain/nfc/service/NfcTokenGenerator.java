package com.option.refit.domain.nfc.service;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

// 태그 식별용 토큰 생성: SecureRandom 32바이트(256비트)를 URL에 안전한 Base64(패딩 없음, 43자)로 변환
@Component
public class NfcTokenGenerator {

    private static final int TOKEN_BYTES = 32;

    private final SecureRandom secureRandom = new SecureRandom();

    public String generate() {
        byte[] bytes = new byte[TOKEN_BYTES];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
