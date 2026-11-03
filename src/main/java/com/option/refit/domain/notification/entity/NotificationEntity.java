package com.option.refit.domain.notification.entity;

import com.option.refit.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "notification")
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
    public NotificationEntity(Long teacherId, Long studentId, String message, String notificationType) {
        this.teacherId = teacherId;
        this.studentId = studentId;
        this.message = message;
        this.notificationType = notificationType;
    }
}
