package com.option.refit.domain.auth.controller;

import com.option.refit.domain.auth.dto.request.EmailLoginRequest;
import com.option.refit.domain.auth.dto.request.EmailRegisterRequest;
import com.option.refit.domain.auth.dto.request.GoogleLoginRequest;
import com.option.refit.domain.auth.dto.request.GoogleRegisterRequest;
import com.option.refit.domain.auth.dto.response.RegisterResponse;
import com.option.refit.domain.auth.dto.response.TokenResponse;
import com.option.refit.domain.auth.service.AuthLoginService;
import com.option.refit.domain.auth.service.AuthRegisterService;
import com.option.refit.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthRegisterService registerService;
    private final AuthLoginService loginService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<RegisterResponse> register(@Valid @RequestBody EmailRegisterRequest request) {
        return ApiResponse.ok(registerService.registerByEmail(request));
    }

    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(@Valid @RequestBody EmailLoginRequest request) {
        return ApiResponse.ok(loginService.loginByEmail(request));
    }

    @PostMapping("/google/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<RegisterResponse> googleRegister(@Valid @RequestBody GoogleRegisterRequest request) {
        return ApiResponse.ok(registerService.registerByGoogle(request));
    }

    @PostMapping("/google/login")
    public ApiResponse<TokenResponse> googleLogin(@Valid @RequestBody GoogleLoginRequest request) {
        return ApiResponse.ok(loginService.loginByGoogle(request));
    }
}
