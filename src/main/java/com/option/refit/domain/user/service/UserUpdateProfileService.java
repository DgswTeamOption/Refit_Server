package com.option.refit.domain.user.service;

import com.option.refit.domain.user.dto.request.UserUpdateProfileRequest;
import com.option.refit.domain.user.dto.response.UserProfileResponse;

public interface UserUpdateProfileService {
    UserProfileResponse updateProfile(UserUpdateProfileRequest request, Long userId);
}
