package com.option.refit.domain.auth.service.impl;

import com.option.refit.domain.auth.dto.response.TokenResponse;
import com.option.refit.domain.auth.service.AuthTokenIssuer;
import com.option.refit.domain.auth.service.AuthTokenService;
import com.option.refit.domain.user.entity.User;
import com.option.refit.domain.user.repository.UserRepository;
import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.jwtError.exception.InvalidTokenSignatureException;
import com.option.refit.global.error.userError.exception.UserNotFoundException;
import com.option.refit.global.jwt.JwtProvider;
import com.option.refit.global.jwt.RefreshTokenStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthTokenServiceImpl implements AuthTokenService {

    private final JwtProvider jwtProvider;
    private final RefreshTokenStore refreshTokenStore;
    private final UserRepository userRepository;
    private final AuthTokenIssuer tokenIssuer;

    @Override
    public TokenResponse refresh(String refreshToken) {
        Long userId = jwtProvider.getUserId(refreshToken); // 누락/만료/위조 시 jwtError 예외

        // Redis에 저장된 토큰과 다르면(로그아웃됨, 재발급으로 교체됨, Access Token 도용 등) 거부
        String stored = refreshTokenStore.find(userId).orElseThrow(InvalidTokenSignatureException::new);
        if (!stored.equals(refreshToken)) {
            throw new InvalidTokenSignatureException();
        }

        User user = userRepository.findByIdAndDeletedAtIsNull(userId).orElseThrow(UserNotFoundException::new);
        return tokenIssuer.issue(user);
    }

    @Override
    public void logout(String refreshToken) {
        try {
            Long userId = jwtProvider.getUserId(refreshToken);
            // 저장된 토큰과 일치할 때만 삭제 (이미 교체된 예전 토큰으로 현재 세션을 끊지 않도록)
            refreshTokenStore.find(userId)
                    .filter(stored -> stored.equals(refreshToken))
                    .ifPresent(stored -> refreshTokenStore.delete(userId));
        } catch (CustomException ignored) {
            // 이미 만료되었거나 잘못된 토큰: 지울 세션이 없으므로 무시
        }
    }
}
