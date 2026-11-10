package com.option.refit.domain.makeup.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.option.refit.domain.makeup.entity.Makeup;
import lombok.Getter;

import java.time.LocalDateTime;

// 보강 일정 조회 응답 (created_at 없음)
@Getter
public class MakeupScheduleResponse {

    private final Long id;

    @JsonProperty("class_id")
    private final Long classId;

    @JsonProperty("student_id")
    private final Long studentId;

    @JsonProperty("teacher_id")
    private final Long teacherId;

    @JsonProperty("schedule_id")
    private final Long scheduleId;

    @JsonProperty("makeup_time")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime makeupTime;

    @JsonProperty("approval_status")
    private final int approvalStatus;

    public MakeupScheduleResponse(Makeup makeup) {
        this.id = makeup.getId();
        this.classId = makeup.getClassId();
        this.studentId = makeup.getStudentId();
        this.teacherId = makeup.getTeacherId();
        this.scheduleId = makeup.getScheduleId();
        this.makeupTime = makeup.getMakeupTime();
        this.approvalStatus = makeup.getApprovalStatus().ordinal();
    }
}
