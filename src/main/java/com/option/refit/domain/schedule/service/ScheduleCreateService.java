package com.option.refit.domain.schedule.service;

import com.option.refit.domain.schedule.dto.ScheduleRequest;
import com.option.refit.global.jwt.JwtPrincipal;

public interface ScheduleCreateService {
    Long createSchedule(JwtPrincipal principal, Long classId, ScheduleRequest.Create request);
}
