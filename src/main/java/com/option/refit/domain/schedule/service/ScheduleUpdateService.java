package com.option.refit.domain.schedule.service;

import com.option.refit.domain.schedule.dto.ScheduleRequest;
import com.option.refit.global.jwt.JwtPrincipal;

public interface ScheduleUpdateService {
    void updateSchedule(JwtPrincipal principal, Long classId, Long scheduleId, ScheduleRequest.Update request);
}
