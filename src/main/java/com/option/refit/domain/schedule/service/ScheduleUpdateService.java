package com.option.refit.domain.schedule.service;

import com.option.refit.domain.schedule.dto.ScheduleRequest;

public interface ScheduleUpdateService {
    void updateSchedule(Long classId, Long scheduleId, ScheduleRequest.CreateOrUpdate request);
}
