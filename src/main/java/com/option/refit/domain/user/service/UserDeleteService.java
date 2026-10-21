package com.option.refit.domain.user.service;

import com.option.refit.domain.user.entity.User;

public interface UserDeleteService {
    User deleteUser(String email);
}
