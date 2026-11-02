package com.option.refit.domain.calendar.entity;

import com.option.refit.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "schedules")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE schedules SET deleted_at = NOW() WHERE id = ?")
public class ScheduleEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "class_id", nullable = false)
    private Long classId;

    @Column(name = "class_name", nullable = false, length = 255)
    private String className;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "schedule_time", nullable = false)
    private LocalDateTime scheduleTime;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder
    public ScheduleEntity(Long classId, String className, String description, LocalDateTime scheduleTime) {
        this.classId = classId;
        this.className = className;
        this.description = description;
        this.scheduleTime = scheduleTime;
    }

    public void updateSchedule(String className, String description, LocalDateTime scheduleTime) {
        this.className = className;
        this.description = description;
        this.scheduleTime = scheduleTime;
    }
}
