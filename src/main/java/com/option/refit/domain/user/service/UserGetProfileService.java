package com.option.refit.domain.user.service;

import com.option.refit.domain.user.dto.response.UserProfileResponse;
import com.option.refit.domain.user.entity.User;

public interface UserGetProfileService {
    UserProfileResponse getUserProfile(User user);
}
