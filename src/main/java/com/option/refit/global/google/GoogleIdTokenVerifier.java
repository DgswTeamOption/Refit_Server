package com.option.refit.global.google;

import com.option.refit.global.error.authError.exception.InvalidGoogleTokenException;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

// 앱이 Google 로그인으로 받은 ID Token을 검증(서명·만료·issuer·audience·이메일 인증 여부)하고 사용자 정보를 꺼낸다
@Component
public class GoogleIdTokenVerifier {

    private static final String JWK_SET_URI = "https://www.googleapis.com/oauth2/v3/certs";
    private static final List<String> ISSUERS = List.of("https://accounts.google.com", "accounts.google.com");

    private final JwtDecoder decoder;

    public GoogleIdTokenVerifier(@Value("${google.client-ids:}") String clientIds) {
        List<String> audiences = Arrays.stream(clientIds.split(","))
                .map(String::trim).filter(s -> !s.isEmpty()).toList();

        NimbusJwtDecoder nimbus = NimbusJwtDecoder.withJwkSetUri(JWK_SET_URI).build();
        OAuth2TokenValidator<Jwt> claims = jwt -> {
            boolean issuerOk = jwt.getIssuer() != null && ISSUERS.contains(jwt.getIssuer().toString());
            boolean audienceOk = jwt.getAudience().stream().anyMatch(audiences::contains);
            return issuerOk && audienceOk
                    ? OAuth2TokenValidatorResult.success()
                    : OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token"));
        };
        nimbus.setJwtValidator(new DelegatingOAuth2TokenValidator<>(new JwtTimestampValidator(), claims));
        this.decoder = nimbus;
    }

    public GoogleUserInfo verify(String idToken) {
        Jwt jwt;
        try {
            jwt = decoder.decode(idToken);
        } catch (RuntimeException e) {
            throw new InvalidGoogleTokenException(e);
        }

        String email = jwt.getClaimAsString("email");
        if (email == null || !Boolean.TRUE.equals(jwt.getClaimAsBoolean("email_verified"))) {
            throw new InvalidGoogleTokenException();
        }
        return new GoogleUserInfo(email, jwt.getClaimAsString("name"), jwt.getClaimAsString("picture"));
    }
}
