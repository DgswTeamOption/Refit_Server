package com.option.refit.domain.auth.service;

import com.option.refit.domain.auth.dto.response.TokenResponse;
import com.option.refit.domain.user.entity.User;
import com.option.refit.global.jwt.JwtProvider;
import com.option.refit.global.jwt.RefreshTokenStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

// Access/Refresh 토큰 발급 + Refresh 토큰 Redis 저장 (로그인 방식과 무관하게 공통)
@Component
@RequiredArgsConstructor
public class AuthTokenIssuer {

    private final JwtProvider jwtProvider;
    private final RefreshTokenStore refreshTokenStore;

    public TokenResponse issue(User user) {
        String access = jwtProvider.createAccessToken(user.getId(), user.getRole());
        String refresh = jwtProvider.createRefreshToken(user.getId());
        refreshTokenStore.save(user.getId(), refresh);
        return new TokenResponse(access, refresh);
    }
}
