package com.option.refit.global.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

import jakarta.servlet.http.HttpServletResponse;

// 웹 클라이언트용 Refresh Token 쿠키 생성/삭제.
// HttpOnly(JS 접근 차단) + Secure + Path=/api/auth(인증 API 요청에만 전송)
@Component
public class RefreshTokenCookie {

    public static final String NAME = "refreshToken";
    private static final String PATH = "/api/auth";

    private final Duration maxAge;
    private final boolean secure;
    private final String sameSite;

    public RefreshTokenCookie(@Value("${jwt.refresh-expiration-ms}") long refreshExpirationMs,
                              @Value("${jwt.refresh-cookie.secure}") boolean secure,
                              @Value("${jwt.refresh-cookie.same-site}") String sameSite) {
        this.maxAge = Duration.ofMillis(refreshExpirationMs);
        this.secure = secure;
        this.sameSite = sameSite;
    }

    public void add(HttpServletResponse response, String refreshToken) {
        response.addHeader(HttpHeaders.SET_COOKIE, build(refreshToken, maxAge).toString());
    }

    // 같은 이름/Path로 만료시간 0 쿠키를 내려 브라우저의 쿠키를 삭제
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
