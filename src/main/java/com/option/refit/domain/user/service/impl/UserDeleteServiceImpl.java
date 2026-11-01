package com.option.refit.domain.user.service.impl;

import com.option.refit.domain.user.entity.User;
import com.option.refit.domain.user.repository.UserRepository;
import com.option.refit.domain.user.service.UserDeleteService;
import com.option.refit.global.error.userError.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDeleteServiceImpl implements UserDeleteService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void deleteUser(User user) {
        User target = userRepository.findByIdAndDeletedAtIsNull(user.getId())
                .orElseThrow(UserNotFoundException::new);
        target.delete();
    }
}
