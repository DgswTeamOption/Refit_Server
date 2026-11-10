package com.option.refit.domain.schedule.service;

import com.option.refit.global.jwt.JwtPrincipal;

public interface ScheduleDeleteService {
    void deleteSchedule(JwtPrincipal principal, Long classId, Long scheduleId);
}
