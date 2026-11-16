package com.option.refit.domain.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

// 웹 클라이언트는 refreshToken을 쿠키로 받으므로 body에서는 null → 응답에서 제외
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TokenResponse(String accessToken, String refreshToken) {

    public TokenResponse withoutRefreshToken() {
        return new TokenResponse(accessToken, null);
    }
}
