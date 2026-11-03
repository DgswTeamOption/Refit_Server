package com.option.refit.domain.notification.repository;

import com.option.refit.domain.notification.entity.NotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    Page<NotificationEntity> findByTeacherId(Long teacherId, Pageable pageable);

    Page<NotificationEntity> findByStudentId(Long studentId, Pageable pageable);
}
