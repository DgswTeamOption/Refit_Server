package com.option.refit.domain.email.controller;

import com.option.refit.domain.email.dto.request.EmailSendCodeRequest;
import com.option.refit.domain.email.dto.request.EmailVerifyCodeRequest;
import com.option.refit.domain.email.dto.response.EmailVerificationResponse;
import com.option.refit.domain.email.service.EmailSendCodeService;
import com.option.refit.domain.email.service.EmailVerifyCodeService;
import com.option.refit.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/email")
public class EmailVerificationController {

    private final EmailSendCodeService emailSendCodeService;
    private final EmailVerifyCodeService emailVerifyCodeService;

    @PostMapping("/send-code")
    public ApiResponse<EmailVerificationResponse> sendCode(@Valid @RequestBody EmailSendCodeRequest request) {
        return ApiResponse.ok(emailSendCodeService.sendCode(request));
    }

    @PostMapping("/verify-code")
    public ApiResponse<EmailVerificationResponse> verifyCode(@Valid @RequestBody EmailVerifyCodeRequest request) {
        return ApiResponse.ok(emailVerifyCodeService.verifyCode(request));
    }
}
