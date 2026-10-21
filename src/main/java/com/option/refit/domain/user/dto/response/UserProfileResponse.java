package com.option.refit.domain.user.dto.response;

import com.option.refit.domain.user.entity.Role;
import com.option.refit.domain.user.entity.User;
import java.time.LocalDate;

public record UserProfileResponse(
        Long id,
        String name,
        String emailId,
        LocalDate birthDate,
        Role role

        // String imageUrl
) {

    public static UserProfileResponse from(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getName(),
                user.getEmailId(),
                user.getBirthDate(),
                user.getRole()
                // user.profileImageUrl()
        );
    }
}
