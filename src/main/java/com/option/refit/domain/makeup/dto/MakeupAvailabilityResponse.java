package com.option.refit.domain.makeup.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.option.refit.domain.makeup.entity.MakeupAvailability;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MakeupAvailabilityResponse {

    private final Long id;

    @JsonProperty("class_id")
    private final Long classId;

    @JsonProperty("teacher_id")
    private final Long teacherId;

    @JsonProperty("schedule_id")
    private final Long scheduleId;

    @JsonProperty("makeup_time")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime makeupTime;

    public MakeupAvailabilityResponse(MakeupAvailability availability) {
        this.id = availability.getId();
        this.classId = availability.getClassId();
        this.teacherId = availability.getTeacherId();
        this.scheduleId = availability.getScheduleId();
        this.makeupTime = availability.getMakeupTime();
    }
}
