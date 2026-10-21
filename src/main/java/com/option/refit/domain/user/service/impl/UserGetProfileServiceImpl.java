package com.option.refit.domain.user.service.impl;

import com.option.refit.domain.user.dto.response.UserProfileResponse;
import com.option.refit.domain.user.entity.User;
import com.option.refit.domain.user.repository.UserRepository;
import com.option.refit.domain.user.service.UserGetProfileService;
import com.option.refit.global.error.userError.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserGetProfileServiceImpl implements UserGetProfileService {
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(User user) {
        User target = userRepository.findByIdAndDeletedAtIsNull(user.getId())
                .orElseThrow(UserNotFoundException::new);
        return UserProfileResponse.from(target);
    }
}
