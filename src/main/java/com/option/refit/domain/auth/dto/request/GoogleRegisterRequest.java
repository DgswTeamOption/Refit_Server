package com.option.refit.domain.auth.dto.request;

import com.option.refit.domain.user.entity.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GoogleRegisterRequest(
        @NotBlank(message = "Google 인증 정보가 필요합니다")
        String idToken,

        @NotNull(message = "역할을 선택해주세요")
        Role role
) {}
