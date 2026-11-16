package com.option.refit.global.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

// Refresh Token을 Redis에 저장/조회/삭제. key: "refresh:{userId}:{client}", TTL: refresh 만료 시간
@Component
public class RefreshTokenStore {

    private static final String KEY_PREFIX = "refresh:";

    private final StringRedisTemplate redisTemplate;
    private final Duration ttl;

    public RefreshTokenStore(StringRedisTemplate redisTemplate,
                             @Value("${jwt.refresh-expiration-ms}") long refreshExpirationMs) {
        this.redisTemplate = redisTemplate;
        this.ttl = Duration.ofMillis(refreshExpirationMs);
    }

    public void save(Long userId, ClientType client, String refreshToken) {
        redisTemplate.opsForValue().set(key(userId, client), refreshToken, ttl);
    }

    public Optional<String> find(Long userId, ClientType client) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(key(userId, client)));
    }

    public void delete(Long userId, ClientType client) {
        redisTemplate.delete(key(userId, client));
    }

    private String key(Long userId, ClientType client) {
        return KEY_PREFIX + userId + ":" + client.name().toLowerCase();
    }
}
