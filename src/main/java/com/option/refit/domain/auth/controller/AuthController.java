package com.option.refit.domain.auth.controller;

import com.option.refit.domain.auth.dto.request.EmailLoginRequest;
import com.option.refit.domain.auth.dto.request.EmailRegisterRequest;
import com.option.refit.domain.auth.dto.request.GoogleLoginRequest;
import com.option.refit.domain.auth.dto.request.GoogleRegisterRequest;
import com.option.refit.domain.auth.dto.response.RegisterResponse;
import com.option.refit.domain.auth.dto.response.TokenResponse;
import com.option.refit.domain.auth.service.AuthLoginService;
import com.option.refit.domain.auth.service.AuthRegisterService;
import com.option.refit.domain.auth.service.AuthTokenService;
import com.option.refit.global.common.ApiResponse;
import com.option.refit.global.error.jwtError.exception.MissingTokenException;
import com.option.refit.global.jwt.RefreshTokenCookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private static final String CLIENT_HEADER = "X-Client";
    private static final String WEB_CLIENT = "web";

    private final AuthRegisterService registerService;
    private final AuthLoginService loginService;
    private final AuthTokenService tokenService;
    private final RefreshTokenCookie refreshTokenCookie;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<RegisterResponse> register(@Valid @RequestBody EmailRegisterRequest request) {
        return ApiResponse.ok(registerService.registerByEmail(request));
    }

    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(
            @Valid @RequestBody EmailLoginRequest request,
            @RequestHeader(value = CLIENT_HEADER, required = false) String client,
            HttpServletResponse response) {
        return ApiResponse.ok(respond(loginService.loginByEmail(request), client, response));
    }

    @PostMapping("/google/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<RegisterResponse> googleRegister(@Valid @RequestBody GoogleRegisterRequest request) {
        return ApiResponse.ok(registerService.registerByGoogle(request));
    }

    @PostMapping("/google/login")
    public ApiResponse<TokenResponse> googleLogin(
            @Valid @RequestBody GoogleLoginRequest request,
            @RequestHeader(value = CLIENT_HEADER, required = false) String client,
            HttpServletResponse response) {
        return ApiResponse.ok(respond(loginService.loginByGoogle(request), client, response));
    }

    // 웹 전용: 쿠키의 Refresh Token으로 Access Token 재발급 (Refresh Token도 쿠키로 교체)
    @PostMapping("/refresh")
    public ApiResponse<TokenResponse> refresh(
            @CookieValue(name = RefreshTokenCookie.NAME, required = false) String refreshToken,
            HttpServletResponse response) {
        if (refreshToken == null) {
            throw new MissingTokenException();
        }
        return ApiResponse.ok(respond(tokenService.refresh(refreshToken), WEB_CLIENT, response));
    }

    // 웹 전용: Redis의 Refresh Token 삭제 + 쿠키 만료
    @PostMapping("/logout")
    public ApiResponse<Void> logout(
            @CookieValue(name = RefreshTokenCookie.NAME, required = false) String refreshToken,
            HttpServletResponse response) {
        if (refreshToken != null) {
            tokenService.logout(refreshToken);
        }
        refreshTokenCookie.expire(response);
        return ApiResponse.ok();
    }

    // X-Client: web 이면 Refresh Token을 쿠키로 내리고 body에서는 제외, 그 외(모바일 등)는 기존처럼 body로 전달
    private TokenResponse respond(TokenResponse tokens, String client, HttpServletResponse response) {
        if (!WEB_CLIENT.equalsIgnoreCase(client)) {
            return tokens;
        }
        refreshTokenCookie.add(response, tokens.refreshToken());
        return tokens.withoutRefreshToken();
    }
}
