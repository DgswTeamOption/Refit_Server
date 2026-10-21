package com.option.refit.domain.user.service.impl;

import com.option.refit.domain.user.dto.response.UserProfileResponse;
import com.option.refit.domain.user.entity.User;
import com.option.refit.domain.user.service.UserGetProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserGetProfileServiceImpl implements UserGetProfileService {
    @Override
    @Transactional
    public UserProfileResponse getUserProfile(User user) {
        return UserProfileResponse.from(user);
    }
}
