package com.option.refit.domain.notification.repository;

import com.option.refit.domain.notification.entity.NotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    Page<NotificationEntity> findByRecipientId(Long recipientId, Pageable pageable);

    Optional<NotificationEntity> findByIdAndRecipientId(
            Long id, Long recipientId);

    // 수신자의 안 읽은(삭제되지 않은) 알림을 한 번에 읽음 처리하고 변경 건수를 반환한다.
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE NotificationEntity n SET n.isRead = true, n.readAt = :now, n.updatedAt = :now " +
            "WHERE n.recipientId = :recipientId AND n.isRead = false AND n.deletedAt IS NULL")
    int markAllAsRead(@Param("recipientId") Long recipientId, @Param("now") LocalDateTime now);
}
