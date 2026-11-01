package com.option.refit.calendar.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.option.refit.calendar.entity.ScheduleEntity;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ScheduleResponse {

    private final Long scheduleId;
    private final Long classId;
    private final String className;
    private final String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime scheduleTime;

    public ScheduleResponse(ScheduleEntity entity){
        this.scheduleId = entity.getId();
        this.classId = entity.getClassId();
        this.className = entity.getClassName();
        this.description = entity.getDescription();
        this.scheduleTime = entity.getScheduleTime();
    }
}
