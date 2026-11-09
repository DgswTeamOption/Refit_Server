package com.option.refit.domain.notification.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.option.refit.domain.notification.entity.NotificationEntity;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NotificationResponse {

    private final Long notificationId;
    private final Long teacherId;
    private final Long studentId;
    private final Long recipientId;
    private final String message;
    private final String notificationType;

    @Getter(onMethod_ =  @JsonProperty("isRead"))
    private final boolean isRead;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime createdAt;

    public NotificationResponse(NotificationEntity entity) {
        this.notificationId = entity.getId();
        this.teacherId = entity.getTeacherId();
        this.studentId = entity.getStudentId();
        this.recipientId = entity.getRecipientId();
        this.message = entity.getMessage();
        this.notificationType = entity.getNotificationType();
        this.isRead = entity.isRead();
        this.createdAt = entity.getCreatedAt();
    }
}
