package com.option.refit.domain.auth.service;

import com.option.refit.domain.auth.dto.request.EmailLoginRequest;
import com.option.refit.domain.auth.dto.request.GoogleLoginRequest;
import com.option.refit.domain.auth.dto.response.TokenResponse;
import com.option.refit.global.jwt.ClientType;

public interface AuthLoginService {
    TokenResponse loginByEmail(EmailLoginRequest request, ClientType client);

    TokenResponse loginByGoogle(GoogleLoginRequest request, ClientType client);
}
