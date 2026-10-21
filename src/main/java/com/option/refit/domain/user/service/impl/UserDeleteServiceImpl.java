package com.option.refit.domain.user.service.impl;

import com.option.refit.domain.user.entity.User;
import com.option.refit.domain.user.service.UserDeleteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDeleteServiceImpl implements UserDeleteService {
    @Override
    @Transactional
    public User deleteUser(String email) {
        return null;
    }
}
