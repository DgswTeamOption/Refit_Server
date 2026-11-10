package com.option.refit.domain.attendance.entity;

import com.option.refit.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

// 일정(schedule) 하나당 출석 기록 하나. 같은 일정에 중복 기록되지 않도록 schedule_id를 유일하게 둔다.
@Entity
@Getter
@Table(
        name = "attendances",
        uniqueConstraints = @UniqueConstraint(columnNames = "schedule_id")
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Attendance extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "class_id", nullable = false)
    private Long classId;

    @Column(name = "schedule_id", nullable = false)
    private Long scheduleId;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    // 일정 시작일 기준 출석 일자 (월별 집계에 사용)
    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AttendanceStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "method", nullable = false, length = 20)
    private AttendanceMethod method;

    // 출석·결석이 기록된 시각
    @Column(name = "checked_at", nullable = false)
    private LocalDateTime checkedAt;

    @Builder
    public Attendance(Long classId, Long scheduleId, Long studentId, LocalDate attendanceDate,
                      AttendanceStatus status, AttendanceMethod method) {
        this.classId = classId;
        this.scheduleId = scheduleId;
        this.studentId = studentId;
        this.attendanceDate = attendanceDate;
        this.status = status;
        this.method = method;
        this.checkedAt = LocalDateTime.now();
    }

    // 선생님이 출석·결석 상태를 직접 바꾼다.
    public void markByTeacher(AttendanceStatus status) {
        this.status = status;
        this.method = AttendanceMethod.MANUAL;
        this.checkedAt = LocalDateTime.now();
    }
}
