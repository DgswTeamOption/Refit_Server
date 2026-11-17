package com.option.refit.domain.auth.controller;

import com.option.refit.domain.auth.dto.request.EmailLoginRequest;
import com.option.refit.domain.auth.dto.request.EmailRegisterRequest;
import com.option.refit.domain.auth.dto.request.GoogleLoginRequest;
import com.option.refit.domain.auth.dto.request.GoogleRegisterRequest;
import com.option.refit.domain.auth.dto.request.RefreshTokenRequest;
import com.option.refit.domain.auth.dto.response.RegisterResponse;
import com.option.refit.domain.auth.dto.response.TokenResponse;
import com.option.refit.domain.auth.service.AuthLoginService;
import com.option.refit.domain.auth.service.AuthRegisterService;
import com.option.refit.domain.auth.service.AuthTokenService;
import com.option.refit.global.common.ApiResponse;
import com.option.refit.global.error.jwtError.exception.MissingTokenException;
import com.option.refit.global.jwt.ClientType;
import com.option.refit.global.jwt.RefreshTokenCookie;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Schema;
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
    private static final String CLIENT_DESC = "클라이언트 종류. web이면 Refresh Token을 쿠키로, 그 외(app, 생략 시 기본값)는 응답 body로 주고받습니다.";
    private static final String COOKIE_NOTE = "웹(X-Client: web)이면 응답에 Set-Cookie(refreshToken)가 내려가고 body에는 Refresh Token이 없습니다. 앱이면 body로 내려갑니다.";

    private final AuthRegisterService registerService;
    private final AuthLoginService loginService;
    private final AuthTokenService tokenService;
    private final RefreshTokenCookie refreshTokenCookie;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<RegisterResponse> register(@Valid @RequestBody EmailRegisterRequest request) {
        return ApiResponse.ok(registerService.registerByEmail(request));
    }

    @Operation(description = COOKIE_NOTE,
            responses = @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                    description = "성공", headers = @Header(name = "Set-Cookie", description = "refreshToken 쿠키 (웹일 때만)")))
    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(
            @Valid @RequestBody EmailLoginRequest request,
            @Parameter(description = CLIENT_DESC, schema = @Schema(allowableValues = {"web", "app"}, defaultValue = "app"))
            @RequestHeader(value = CLIENT_HEADER, required = false) String clientHeader,
            HttpServletResponse response) {
        ClientType client = ClientType.from(clientHeader);
        return ApiResponse.ok(respond(loginService.loginByEmail(request, client), client, response));
    }

    @PostMapping("/google/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<RegisterResponse> googleRegister(@Valid @RequestBody GoogleRegisterRequest request) {
        return ApiResponse.ok(registerService.registerByGoogle(request));
    }

    @Operation(description = COOKIE_NOTE,
            responses = @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                    description = "성공", headers = @Header(name = "Set-Cookie", description = "refreshToken 쿠키 (웹일 때만)")))
    @PostMapping("/google/login")
    public ApiResponse<TokenResponse> googleLogin(
            @Valid @RequestBody GoogleLoginRequest request,
            @Parameter(description = CLIENT_DESC, schema = @Schema(allowableValues = {"web", "app"}, defaultValue = "app"))
            @RequestHeader(value = CLIENT_HEADER, required = false) String clientHeader,
            HttpServletResponse response) {
        ClientType client = ClientType.from(clientHeader);
        return ApiResponse.ok(respond(loginService.loginByGoogle(request, client), client, response));
    }

    // Access Token 재발급 (Refresh Token도 교체). 웹: 쿠키로 받고 쿠키로 전달 / 앱: body로 받고 body로 전달
    @Operation(description = "Refresh Token 우선순위: 웹(X-Client: web)은 쿠키만, 앱은 body만 사용합니다(서로 대체 불가). " + COOKIE_NOTE,
            responses = @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                    description = "성공", headers = @Header(name = "Set-Cookie", description = "새 refreshToken 쿠키 (웹일 때만)")))
    @PostMapping("/refresh")
    public ApiResponse<TokenResponse> refresh(
            @Parameter(description = CLIENT_DESC, schema = @Schema(allowableValues = {"web", "app"}, defaultValue = "app"))
            @RequestHeader(value = CLIENT_HEADER, required = false) String clientHeader,
            @CookieValue(name = RefreshTokenCookie.NAME, required = false) String cookieToken,
            @RequestBody(required = false) RefreshTokenRequest body,
            HttpServletResponse response) {
        ClientType client = ClientType.from(clientHeader);
        String refreshToken = resolveRefreshToken(client, cookieToken, body);
        if (refreshToken == null) {
            throw new MissingTokenException();
        }
        return ApiResponse.ok(respond(tokenService.refresh(refreshToken, client), client, response));
    }

    // Redis의 Refresh Token 삭제. 웹이면 쿠키도 만료시킴
    @Operation(description = "웹은 쿠키, 앱은 body의 Refresh Token을 사용합니다. 웹이면 refreshToken 쿠키를 만료시킵니다(Set-Cookie).")
    @PostMapping("/logout")
    public ApiResponse<Void> logout(
            @Parameter(description = CLIENT_DESC, schema = @Schema(allowableValues = {"web", "app"}, defaultValue = "app"))
            @RequestHeader(value = CLIENT_HEADER, required = false) String clientHeader,
            @CookieValue(name = RefreshTokenCookie.NAME, required = false) String cookieToken,
            @RequestBody(required = false) RefreshTokenRequest body,
            HttpServletResponse response) {
        ClientType client = ClientType.from(clientHeader);
        String refreshToken = resolveRefreshToken(client, cookieToken, body);
        if (refreshToken != null) {
            tokenService.logout(refreshToken, client);
        }
        if (client == ClientType.WEB) {
            refreshTokenCookie.expire(response);
        }
        return ApiResponse.ok();
    }

    // 웹은 쿠키, 앱은 body에서 Refresh Token을 꺼낸다. 없거나 비어 있으면 null
    private String resolveRefreshToken(ClientType client, String cookieToken, RefreshTokenRequest body) {
        String token = client == ClientType.WEB ? cookieToken : (body == null ? null : body.refreshToken());
        return token == null || token.isBlank() ? null : token;
    }

    // 웹이면 Refresh Token을 쿠키로 내리고 body에서는 제외, 앱이면 body로 전달
    private TokenResponse respond(TokenResponse tokens, ClientType client, HttpServletResponse response) {
        if (client != ClientType.WEB) {
            return tokens;
        }
        refreshTokenCookie.add(response, tokens.refreshToken());
        return tokens.withoutRefreshToken();
    }
}
