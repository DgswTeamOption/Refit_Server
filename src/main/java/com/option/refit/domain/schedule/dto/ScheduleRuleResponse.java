package com.option.refit.domain.schedule.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.option.refit.domain.schedule.entity.ScheduleRuleEntity;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
public class ScheduleRuleResponse {

    private final Long ruleId;
    private final Long classId;
    private final Long studentId;
    private final DayOfWeek dayOfWeek;

    @JsonFormat(pattern = "HH:mm")
    private final LocalTime startTime;

    @JsonFormat(pattern = "HH:mm")
    private final LocalTime endTime;

    public ScheduleRuleResponse(ScheduleRuleEntity entity) {
        this.ruleId = entity.getId();
        this.classId = entity.getClassId();
        this.studentId = entity.getStudentId();
        this.dayOfWeek = entity.getDayOfWeek();
        this.startTime = entity.getStartTime();
        this.endTime = entity.getEndTime();
    }
}
