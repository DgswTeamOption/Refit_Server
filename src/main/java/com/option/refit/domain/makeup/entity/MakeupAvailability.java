package com.option.refit.domain.makeup.entity;

import com.option.refit.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// 선생님이 등록한 보강 가능 시간대 (시간대별 정원 포함)
@Entity
@Getter
@Table(
        name = "makeup_availabilities",
        uniqueConstraints = @UniqueConstraint(columnNames = {"class_id", "schedule_id", "makeup_time"})
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MakeupAvailability extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "class_id", nullable = false)
    private Long classId;

    @Column(name = "teacher_id", nullable = false)
    private Long teacherId;

    @Column(name = "schedule_id", nullable = false)
    private Long scheduleId;

    @Column(name = "makeup_time", nullable = false)
    private LocalDateTime makeupTime;

    // 해당 시간대에 신청 가능한 최대 인원
    @Column(name = "capacity", nullable = false)
    private int capacity;

    public void changeMakeupTime(LocalDateTime makeupTime) {
        this.makeupTime = makeupTime;
    }

    public void changeCapacity(int capacity) {
        this.capacity = capacity;
    }

    @Builder
    public MakeupAvailability(Long classId, Long teacherId, Long scheduleId, LocalDateTime makeupTime, int capacity) {
        this.classId = classId;
        this.teacherId = teacherId;
        this.scheduleId = scheduleId;
        this.makeupTime = makeupTime;
        this.capacity = capacity;
    }
}
