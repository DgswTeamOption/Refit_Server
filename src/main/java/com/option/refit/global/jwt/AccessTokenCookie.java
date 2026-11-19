package com.option.refit.global.jwt;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

// 웹 클라이언트용 Access Token 쿠키 생성/삭제.
// HttpOnly(JS 접근 차단) + Secure + Path=/(모든 API 요청에 전송). 설정은 Refresh Token 쿠키와 동일
@Component
public class AccessTokenCookie {

    public static final String NAME = "accessToken";
    private static final String PATH = "/";

    private final Duration maxAge;
    private final boolean secure;
    private final String sameSite;

    public AccessTokenCookie(@Value("${jwt.access-expiration-ms}") long accessExpirationMs,
                             @Value("${jwt.refresh-cookie.secure}") boolean secure,
                             @Value("${jwt.refresh-cookie.same-site}") String sameSite) {
        this.maxAge = Duration.ofMillis(accessExpirationMs);
        this.secure = secure;
        this.sameSite = sameSite;
    }

    public void add(HttpServletResponse response, String accessToken) {
        response.addHeader(HttpHeaders.SET_COOKIE, build(accessToken, maxAge).toString());
    }

    public void expire(HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE, build("", Duration.ZERO).toString());
    }

    private ResponseCookie build(String value, Duration maxAge) {
        return ResponseCookie.from(NAME, value)
                .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite)
                .path(PATH)
                .maxAge(maxAge)
                .build();
    }
}
