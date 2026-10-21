package com.option.refit.domain.user.service.impl;

import com.option.refit.domain.user.dto.request.UserChangeProfileImageRequest;
import com.option.refit.domain.user.dto.response.UserProfileResponse;
import com.option.refit.domain.user.service.UserChangeProfileImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserChangeProfileImageServiceImpl implements UserChangeProfileImageService {
    @Override
    public UserProfileResponse changeProfileImage(UserChangeProfileImageRequest request, Long userId) {
        return null;
    }
}
