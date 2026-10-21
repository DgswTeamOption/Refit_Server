package com.option.refit.domain.user.service;

import com.option.refit.domain.user.dto.request.UserUpdateProfileRequest;
import com.option.refit.domain.user.dto.response.UserProfileResponse;
import com.option.refit.domain.user.entity.User;

public interface UserUpdateProfileService {
    UserProfileResponse updateProfile(UserUpdateProfileRequest request, User user);
}
