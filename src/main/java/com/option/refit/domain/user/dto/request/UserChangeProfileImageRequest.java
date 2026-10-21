package com.option.refit.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UserChangeProfileImageRequest(
        @NotBlank
        String profileImageUrl
) {}
