package com.option.refit.domain.auth.service;

import com.option.refit.domain.auth.dto.response.TokenResponse;
import com.option.refit.global.jwt.ClientType;

public interface AuthTokenService {
    // Refresh Token 검증 후 Access/Refresh 토큰 재발급 (Refresh Token Rotation)
    TokenResponse refresh(String refreshToken, ClientType client);

    // Redis의 Refresh Token 삭제. 이미 만료/잘못된 토큰이어도 예외 없이 종료
    void logout(String refreshToken, ClientType client);
}
