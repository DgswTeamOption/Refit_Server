package com.option.refit.domain.notification.entity;

import com.option.refit.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "notification", indexes = {
        @Index(name = "idx_notification_recipient_created_id", columnList = "recipient_id, created_at, id")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE notification SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")

public class NotificationEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "teacher_id", nullable = false)
    private Long teacherId;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    // teacherId/studentId는 알림 문맥이며, 실제 수신자는 recipientId로 구분한다.
    @Column(name = "recipient_id", nullable = false)
    private Long recipientId;

    @Column(name = "message", nullable = false, length = 255)
    private String message;

    // 0: 보강 제안, 1: 보강 승인, 2: 보강 거절, 3: 공지 수신, 4: 수업 일정 변경 수신
    @Column(name = "notification_type", nullable = false, length = 1, columnDefinition = "CHAR(1)")
    private String notificationType;

    @Column(name = "is_read", nullable = false)
    @ColumnDefault("false")
    private boolean isRead = false;

    @Column(name = "Read_at")
    private LocalDateTime readAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder
    public NotificationEntity(Long teacherId, Long studentId, @NonNull Long recipientId,
                              String message, String notificationType) {
        this.teacherId = teacherId;
        this.studentId = studentId;
        this.recipientId = recipientId;
        this.message = message;
        this.notificationType = notificationType;
    }
}