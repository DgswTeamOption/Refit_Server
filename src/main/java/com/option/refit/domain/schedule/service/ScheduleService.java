package com.option.refit.domain.schedule.service;

import com.option.refit.domain.schedule.dto.ScheduleRequest;
import com.option.refit.domain.schedule.dto.ScheduleResponse;

import java.util.List;

public interface ScheduleService {
    List<ScheduleResponse> getSchedulesByClass(Long classId);
    List<ScheduleResponse> getMonthlyCalendar(Long classId, int year, int month);
    Long createSchedule(Long classId, ScheduleRequest.CreateOrUpdate request);
    void updateSchedule(Long classId, Long scheduleId, ScheduleRequest.CreateOrUpdate request);
    void deleteSchedule(Long classId, Long scheduleId);
}
