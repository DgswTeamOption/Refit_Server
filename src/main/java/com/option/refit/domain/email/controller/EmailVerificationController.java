package com.option.refit.domain.email.controller;

import com.option.refit.domain.email.dto.request.EmailSendCodeRequest;
import com.option.refit.domain.email.dto.request.EmailVerifyCodeRequest;
import com.option.refit.domain.email.dto.response.EmailVerificationResponse;
import com.option.refit.domain.email.service.EmailSendCodeService;
import com.option.refit.domain.email.service.EmailVerifyCodeService;
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
@RequestMapping("/api/user/email")
public class EmailVerificationController {

    private final EmailSendCodeService emailSendCodeService;
    private final EmailVerifyCodeService emailVerifyCodeService;

    // 메일 발송 "접수"를 의미하므로 202. 실제 발송 성공 여부는 응답에 포함되지 않는다
    @PostMapping("/send-code")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ApiResponse<EmailVerificationResponse> sendCode(@Valid @RequestBody EmailSendCodeRequest request) {
        return ApiResponse.ok(emailSendCodeService.sendCode(request));
    }

    @PostMapping("/verify-code")
    public ApiResponse<EmailVerificationResponse> verifyCode(@Valid @RequestBody EmailVerifyCodeRequest request) {
        return ApiResponse.ok(emailVerifyCodeService.verifyCode(request));
    }
}
