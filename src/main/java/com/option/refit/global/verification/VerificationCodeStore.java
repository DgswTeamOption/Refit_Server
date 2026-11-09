package com.option.refit.global.verification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

// 이메일 인증코드를 Redis에 저장/조회/삭제. TTL 만료 시 자동 삭제
// - code:     "verification:code:{email}"     TTL: 인증코드 만료 시간
// - attempts: "verification:attempts:{email}" TTL: 인증코드 만료 시간 (코드 재발급 시 초기화)
// - verified: "verification:verified:{email}" TTL: 인증 완료 유지 시간 (회원가입/로그인 단계에서 확인)
@Component
public class VerificationCodeStore {

    private static final String CODE_PREFIX = "verification:code:";
    private static final String ATTEMPTS_PREFIX = "verification:attempts:";
    private static final String VERIFIED_PREFIX = "verification:verified:";

    private final StringRedisTemplate redisTemplate;
    private final Duration codeTtl;
    private final Duration verifiedTtl;

    public VerificationCodeStore(StringRedisTemplate redisTemplate,
                                 @Value("${jwt.email-code-expiration-ms}") long codeExpirationMs,
                                 @Value("${jwt.email-verified-expiration-ms}") long verifiedExpirationMs) {
        this.redisTemplate = redisTemplate;
        this.codeTtl = Duration.ofMillis(codeExpirationMs);
        this.verifiedTtl = Duration.ofMillis(verifiedExpirationMs);
    }

    // 새 코드 저장. 기존 코드/시도 횟수/인증 완료 상태는 모두 초기화
    public void saveCode(String email, String code) {
        redisTemplate.opsForValue().set(CODE_PREFIX + email, code, codeTtl);
        redisTemplate.delete(ATTEMPTS_PREFIX + email);
        redisTemplate.delete(VERIFIED_PREFIX + email);
    }

    public Optional<String> findCode(String email) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(CODE_PREFIX + email));
    }

    // 실패 횟수를 1 증가시키고 증가된 값을 반환
    public long increaseAttempts(String email) {
        String key = ATTEMPTS_PREFIX + email;
        Long attempts = redisTemplate.opsForValue().increment(key);
        redisTemplate.expire(key, codeTtl);
        return attempts == null ? 0L : attempts;
    }

    public void deleteCode(String email) {
        redisTemplate.delete(CODE_PREFIX + email);
        redisTemplate.delete(ATTEMPTS_PREFIX + email);
    }

    public void markVerified(String email) {
        redisTemplate.opsForValue().set(VERIFIED_PREFIX + email, "true", verifiedTtl);
    }

    public boolean isVerified(String email) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(VERIFIED_PREFIX + email));
    }

    public void deleteVerified(String email) {
        redisTemplate.delete(VERIFIED_PREFIX + email);
    }
}
