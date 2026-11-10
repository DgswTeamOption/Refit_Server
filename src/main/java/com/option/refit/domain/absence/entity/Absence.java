package com.option.refit.domain.absence.entity;

import com.option.refit.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "absences")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE absences SET deleted_at = NOW() WHERE id = ?")
public class Absence extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 수업/학생은 ScheduleEntity와 동일하게 ID로 참조 (수업 엔티티가 아직 develop에 없음)
    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "class_id", nullable = false)
    private Long classId;

    @Column(name = "absence_date", nullable = false)
    private LocalDate absenceDate;

    @Column(name = "reason", nullable = false, length = 500)
    private String reason;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status", nullable = false)
    private AbsenceStatus status;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder
    public Absence(Long studentId, Long classId, LocalDate absenceDate, String reason) {
        this.studentId = studentId;
        this.classId = classId;
        this.absenceDate = absenceDate;
        this.reason = reason;
        this.status = AbsenceStatus.PENDING;
    }

    public boolean isPending() {
        return this.status == AbsenceStatus.PENDING;
    }

    public boolean isOwnedBy(Long userId) {
        return this.studentId.equals(userId);
    }

    public void approve() {
        this.status = AbsenceStatus.APPROVED;
    }

    public void reject() {
        this.status = AbsenceStatus.REJECTED;
    }

    public void cancel() {
        this.status = AbsenceStatus.CANCELED;
    }
}
