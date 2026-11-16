package com.option.refit.domain.auth.dto.request;

// 앱(모바일) 전용: 웹은 쿠키로 Refresh Token을 보내므로 사용하지 않음
public record RefreshTokenRequest(String refreshToken) {}
