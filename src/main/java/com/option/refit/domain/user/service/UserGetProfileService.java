package com.option.refit.domain.user.service;

import com.option.refit.domain.user.dto.response.UserProfileResponse;

public interface UserGetProfileService {
    UserProfileResponse getUserProfile(Long userId);
}
