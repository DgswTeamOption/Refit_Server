package com.option.refit.domain.user.repository;


import com.option.refit.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);

    boolean existsByStudentNo(String studentNo);
}
