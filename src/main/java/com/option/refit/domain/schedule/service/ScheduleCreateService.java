package com.option.refit.domain.schedule.service;

import com.option.refit.domain.schedule.dto.ScheduleRequest;

public interface ScheduleCreateService {
    Long createSchedule(Long classId, ScheduleRequest.CreateOrUpdate request);
}
