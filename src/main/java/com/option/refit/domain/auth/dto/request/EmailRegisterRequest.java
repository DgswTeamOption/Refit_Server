package com.option.refit.domain.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.option.refit.domain.user.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// verificationToken = 이메일로 받은 6자리 인증코드
public record EmailRegisterRequest(
        @JsonProperty("email_id")
        @NotBlank(message = "이메일을 입력해주세요")
        @Email(message = "이메일 형식이 올바르지 않습니다")
        @Size(max = 255, message = "이메일은 255자 이하로 해주세요")
        String emailId,

        @NotBlank(message = "이름을 입력해주세요")
        @Size(max = 50, message = "이름은 50자 이하로 해주세요")
        String name,

        @NotNull(message = "역할을 선택해주세요")
        Role role,

        @NotBlank(message = "인증코드를 입력해주세요")
        @Pattern(regexp = "^\\d{6}$", message = "인증코드는 6자리 숫자여야 합니다")
        String verificationToken
) {}
