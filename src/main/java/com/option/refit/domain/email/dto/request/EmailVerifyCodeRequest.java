package com.option.refit.domain.email.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record EmailVerifyCodeRequest(
        @JsonProperty("email_id")
        @NotBlank(message = "이메일을 입력해주세요")
        @Email(message = "이메일 형식이 올바르지 않습니다")
        String emailId,

        @JsonProperty("verification_code")
        @NotBlank(message = "인증코드를 입력해주세요")
        @Pattern(regexp = "^\\d{6}$", message = "인증코드는 6자리 숫자여야 합니다")
        String verificationCode
) {}
