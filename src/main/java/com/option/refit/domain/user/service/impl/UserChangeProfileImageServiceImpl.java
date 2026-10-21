package com.option.refit.domain.user.service.impl;

import com.option.refit.domain.user.dto.request.UserChangeProfileImageRequest;
import com.option.refit.domain.user.dto.response.UserProfileResponse;
import com.option.refit.domain.user.entity.User;
import com.option.refit.domain.user.repository.UserRepository;
import com.option.refit.domain.user.service.UserChangeProfileImageService;
import com.option.refit.global.error.userError.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserChangeProfileImageServiceImpl implements UserChangeProfileImageService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserProfileResponse changeProfileImage(UserChangeProfileImageRequest request, Long userId) {
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(UserNotFoundException::new);
        user.changeProfileImage(request.profileImageUrl());
        return UserProfileResponse.from(user);
    }
}
