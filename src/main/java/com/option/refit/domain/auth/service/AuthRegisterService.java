package com.option.refit.domain.auth.service;

import com.option.refit.domain.auth.dto.request.EmailRegisterRequest;
import com.option.refit.domain.auth.dto.request.GoogleRegisterRequest;
import com.option.refit.domain.auth.dto.response.RegisterResponse;

public interface AuthRegisterService {
    RegisterResponse registerByEmail(EmailRegisterRequest request);

    RegisterResponse registerByGoogle(GoogleRegisterRequest request);
}
