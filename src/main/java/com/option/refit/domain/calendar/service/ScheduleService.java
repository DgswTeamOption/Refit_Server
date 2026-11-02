package com.option.refit.domain.calendar.service;

import com.option.refit.domain.calendar.dto.ScheduleRequest;
import com.option.refit.domain.calendar.dto.ScheduleResponse;

import java.util.List;

public interface ScheduleService {
    List<ScheduleResponse> getSchedulesByClass(Long classId);
    List<ScheduleResponse> getMonthlyCalendar(Long classId, int year, int month);
    Long createSchedule(Long classId, ScheduleRequest.CreateOrUpdate request);
    void updateSchedule(Long classId, Long scheduleId, ScheduleRequest.CreateOrUpdate request);
    void deleteSchedule(Long classId, Long scheduleId);
}
