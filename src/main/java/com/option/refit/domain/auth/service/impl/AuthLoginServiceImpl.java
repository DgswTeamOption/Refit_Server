package com.option.refit.domain.auth.service.impl;

import com.option.refit.domain.auth.dto.request.EmailLoginRequest;
import com.option.refit.domain.auth.dto.request.GoogleLoginRequest;
import com.option.refit.domain.auth.dto.response.TokenResponse;
import com.option.refit.domain.auth.service.AuthLoginService;
import com.option.refit.domain.auth.service.AuthTokenIssuer;
import com.option.refit.domain.email.dto.request.EmailVerifyCodeRequest;
import com.option.refit.domain.email.service.EmailVerifyCodeService;
import com.option.refit.domain.user.entity.User;
import com.option.refit.domain.user.repository.UserRepository;
import com.option.refit.global.error.authError.exception.InactiveUserException;
import com.option.refit.global.error.userError.exception.UserNotFoundException;
import com.option.refit.global.google.GoogleIdTokenVerifier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthLoginServiceImpl implements AuthLoginService {

    private final UserRepository userRepository;
    private final EmailVerifyCodeService emailVerifyCodeService;
    private final GoogleIdTokenVerifier googleIdTokenVerifier;
    private final AuthTokenIssuer tokenIssuer;

    @Override
    public TokenResponse loginByEmail(EmailLoginRequest request) {
        // 코드 검증을 먼저 해서, 코드 없이 가입 여부를 알아내지 못하게 한다
        emailVerifyCodeService.verifyCode(new EmailVerifyCodeRequest(request.emailId(), request.verificationToken()));
        return issueFor(request.emailId());
    }

    @Override
    public TokenResponse loginByGoogle(GoogleLoginRequest request) {
        return issueFor(googleIdTokenVerifier.verify(request.idToken()).email());
    }

    // 가입되지 않은 이메일은 404 → 앱이 회원가입(역할 선택) 화면으로 안내
    private TokenResponse issueFor(String email) {
        User user = userRepository.findByEmailId(email).orElseThrow(UserNotFoundException::new);
        if (user.getDeletedAt() != null) {
            throw new InactiveUserException();
        }
        return tokenIssuer.issue(user);
    }
}
