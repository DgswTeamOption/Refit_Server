package com.option.refit.domain.auth.service;

import com.option.refit.domain.auth.dto.request.EmailLoginRequest;
import com.option.refit.domain.auth.dto.request.GoogleLoginRequest;
import com.option.refit.domain.auth.dto.response.TokenResponse;

public interface AuthLoginService {
    TokenResponse loginByEmail(EmailLoginRequest request);

    TokenResponse loginByGoogle(GoogleLoginRequest request);
}
