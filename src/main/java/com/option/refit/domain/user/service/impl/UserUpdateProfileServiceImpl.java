package com.option.refit.domain.user.service.impl;

import com.option.refit.domain.user.dto.request.UserUpdateProfileRequest;
import com.option.refit.domain.user.dto.response.UserProfileResponse;
import com.option.refit.domain.user.entity.User;
import com.option.refit.domain.user.repository.UserRepository;
import com.option.refit.domain.user.service.UserUpdateProfileService;
import com.option.refit.global.error.userError.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserUpdateProfileServiceImpl implements UserUpdateProfileService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserProfileResponse updateProfile(UserUpdateProfileRequest request, User user) {
        User target = userRepository.findByIdAndDeletedAtIsNull(user.getId())
                .orElseThrow(UserNotFoundException::new);
        target.updateProfile(request.name());
        return UserProfileResponse.from(target);
    }
}
