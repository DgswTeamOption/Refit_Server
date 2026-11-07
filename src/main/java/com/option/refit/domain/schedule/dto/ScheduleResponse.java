package com.option.refit.domain.schedule.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.option.refit.domain.schedule.entity.ScheduleEntity;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ScheduleResponse {

    private final Long scheduleId;
    private final Long classId;
    private final Long studentId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime endTime;

    public ScheduleResponse(ScheduleEntity entity) {
        this.scheduleId = entity.getId();
        this.classId = entity.getClassId();
        this.studentId = entity.getStudentId();
        this.startTime = entity.getStartTime();
        this.endTime = entity.getEndTime();
    }
}
