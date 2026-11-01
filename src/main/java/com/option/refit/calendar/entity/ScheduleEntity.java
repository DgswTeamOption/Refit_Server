package com.option.refit.calendar.entity;

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
public class ScheduleEntity extends Base {

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
