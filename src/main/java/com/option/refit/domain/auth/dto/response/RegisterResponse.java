package com.option.refit.domain.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.option.refit.domain.user.entity.Role;
import com.option.refit.domain.user.entity.User;

public record RegisterResponse(
        Long id,
        @JsonProperty("email_id") String emailId,
        String name,
        Role role
) {
    public static RegisterResponse from(User user) {
        return new RegisterResponse(user.getId(), user.getEmailId(), user.getName(), user.getRole());
    }
}
