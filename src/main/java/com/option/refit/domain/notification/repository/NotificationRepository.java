package com.option.refit.domain.notification.repository;

import com.option.refit.domain.notification.entity.NotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    Page<NotificationEntity> findByRecipientId(Long recipientId, Pageable pageable);

    Optional<NotificationEntity> findByIdAndRecipientId(
            Long id, Long recipientId);
}