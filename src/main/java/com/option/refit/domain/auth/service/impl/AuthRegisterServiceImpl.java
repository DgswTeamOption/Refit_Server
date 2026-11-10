package com.option.refit.domain.auth.service.impl;

import com.option.refit.domain.auth.dto.request.EmailRegisterRequest;
import com.option.refit.domain.auth.dto.request.GoogleRegisterRequest;
import com.option.refit.domain.auth.dto.response.RegisterResponse;
import com.option.refit.domain.auth.service.AuthRegisterService;
import com.option.refit.domain.email.dto.request.EmailVerifyCodeRequest;
import com.option.refit.domain.email.service.EmailVerifyCodeService;
import com.option.refit.domain.user.entity.Role;
import com.option.refit.domain.user.entity.User;
import com.option.refit.domain.user.repository.UserRepository;
import com.option.refit.global.error.authError.exception.DuplicateEmailException;
import com.option.refit.global.google.GoogleIdTokenVerifier;
import com.option.refit.global.google.GoogleUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthRegisterServiceImpl implements AuthRegisterService {

    private static final int MAX_NAME_LENGTH = 50;

    private final UserRepository userRepository;
    private final EmailVerifyCodeService emailVerifyCodeService;
    private final GoogleIdTokenVerifier googleIdTokenVerifier;

    @Override
    @Transactional
    public RegisterResponse registerByEmail(EmailRegisterRequest request) {
        // 중복 가입이면 인증코드를 소모하지 않도록 먼저 확인
        if (userRepository.existsByEmailId(request.emailId())) {
            throw new DuplicateEmailException();
        }
        emailVerifyCodeService.verifyCode(new EmailVerifyCodeRequest(request.emailId(), request.verificationToken()));
        return create(request.emailId(), request.name(), request.role(), null);
    }

    @Override
    @Transactional
    public RegisterResponse registerByGoogle(GoogleRegisterRequest request) {
        GoogleUserInfo info = googleIdTokenVerifier.verify(request.idToken());
        if (userRepository.existsByEmailId(info.email())) {
            throw new DuplicateEmailException();
        }
        String name = info.name() == null || info.name().isBlank() ? info.email().split("@")[0] : info.name();
        if (name.length() > MAX_NAME_LENGTH) {
            name = name.substring(0, MAX_NAME_LENGTH);
        }
        return create(info.email(), name, request.role(), info.pictureUrl());
    }

    private RegisterResponse create(String email, String name, Role role, String profileImageUrl) {
        try {
            User user = userRepository.saveAndFlush(User.builder()
                    .emailId(email).name(name).role(role).profileImageUrl(profileImageUrl).build());
            return RegisterResponse.from(user);
        } catch (DataIntegrityViolationException e) { // 동시 가입 경합
            throw new DuplicateEmailException();
        }
    }
}
