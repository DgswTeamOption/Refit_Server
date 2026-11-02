package com.option.refit.domain.schedule.service;

import com.option.refit.domain.schedule.dto.ScheduleResponse;

import java.util.List;

public interface ScheduleGetService {
    List<ScheduleResponse> getSchedulesByClass(Long classId);
    List<ScheduleResponse> getMonthlyCalendar(Long classId, int year, int month);
}
