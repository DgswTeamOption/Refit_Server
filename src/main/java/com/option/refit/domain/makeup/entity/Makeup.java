package com.option.refit.domain.makeup.entity;

import com.option.refit.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// 학생의 보강 신청
@Entity
@Getter
@Table(name = "makeups")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Makeup extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "teacher_id", nullable = false)
    private Long teacherId;

    @Column(name = "class_id", nullable = false)
    private Long classId;

    @Column(name = "schedule_id", nullable = false)
    private Long scheduleId;

    @Column(name = "makeup_time", nullable = false)
    private LocalDateTime makeupTime;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "approval_status", nullable = false)
    private MakeupApprovalStatus approvalStatus;

    @Builder
    public Makeup(Long studentId, Long teacherId, Long classId, Long scheduleId, LocalDateTime makeupTime) {
        this.studentId = studentId;
        this.teacherId = teacherId;
        this.classId = classId;
        this.scheduleId = scheduleId;
        this.makeupTime = makeupTime;
        this.approvalStatus = MakeupApprovalStatus.PENDING;
    }

    public boolean isPending() {
        return this.approvalStatus == MakeupApprovalStatus.PENDING;
    }

    public void approve() {
        this.approvalStatus = MakeupApprovalStatus.APPROVED;
    }

    public void reject() {
        this.approvalStatus = MakeupApprovalStatus.REJECTED;
    }

    // 학생이 대기 중인 신청을 취소한다. 행은 삭제하지 않고 상태만 바꿔 이력(생성일시 등)을 유지한다.
    public void cancel() {
        this.approvalStatus = MakeupApprovalStatus.CANCELLED;
    }
}
