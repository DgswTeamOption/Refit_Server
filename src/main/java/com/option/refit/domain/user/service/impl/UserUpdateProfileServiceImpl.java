package com.option.refit.domain.user.service.impl;

import com.option.refit.domain.user.dto.request.UserUpdateProfileRequest;
import com.option.refit.domain.user.dto.response.UserProfileResponse;
import com.option.refit.domain.user.entity.User;
import com.option.refit.domain.user.repository.UserRepository;
import com.option.refit.domain.user.service.UserUpdateProfileService;
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
        user.updateProfile(request.name(), request.birthDate());
        return UserProfileResponse.from(userRepository.save(user));
    }
}
