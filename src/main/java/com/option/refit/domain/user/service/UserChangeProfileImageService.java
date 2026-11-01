package com.option.refit.domain.user.service;

import com.option.refit.domain.user.dto.request.UserChangeProfileImageRequest;
import com.option.refit.domain.user.dto.response.UserProfileResponse;

public interface UserChangeProfileImageService {
    UserProfileResponse changeProfileImage(UserChangeProfileImageRequest request, Long userId);
}
