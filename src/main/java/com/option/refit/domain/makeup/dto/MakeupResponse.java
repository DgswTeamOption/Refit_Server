package com.option.refit.domain.makeup.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.option.refit.domain.makeup.entity.Makeup;
import lombok.Getter;

import java.time.LocalDateTime;

// 보강 신청 / 보강 목록 조회 응답 (approval_status: 0 대기, 1 승인, 2 거절)
@Getter
public class MakeupResponse {

    private final Long id;

    @JsonProperty("student_id")
    private final Long studentId;

    @JsonProperty("teacher_id")
    private final Long teacherId;

    @JsonProperty("class_id")
    private final Long classId;

    @JsonProperty("schedule_id")
    private final Long scheduleId;

    @JsonProperty("makeup_time")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime makeupTime;

    @JsonProperty("approval_status")
    private final int approvalStatus;

    @JsonProperty("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime createdAt;

    public MakeupResponse(Makeup makeup) {
        this.id = makeup.getId();
        this.studentId = makeup.getStudentId();
        this.teacherId = makeup.getTeacherId();
        this.classId = makeup.getClassId();
        this.scheduleId = makeup.getScheduleId();
        this.makeupTime = makeup.getMakeupTime();
        this.approvalStatus = makeup.getApprovalStatus().ordinal();
        this.createdAt = makeup.getCreatedAt();
    }
}
