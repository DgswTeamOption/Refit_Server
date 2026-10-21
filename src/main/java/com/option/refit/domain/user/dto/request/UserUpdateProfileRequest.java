package com.option.refit.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record UserUpdateProfileRequest(
        @NotBlank
        @Size(max = 50, message = "이름은 50자 이하로 해주세요")
        String name
) {}
