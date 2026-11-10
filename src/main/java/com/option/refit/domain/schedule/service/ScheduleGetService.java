package com.option.refit.domain.schedule.service;

import com.option.refit.domain.schedule.dto.ScheduleResponse;
import com.option.refit.global.jwt.JwtPrincipal;

import java.util.List;

// 담당 교사는 수업 전체 일정, 참여 학생은 본인 일정만 조회
public interface ScheduleGetService {
    List<ScheduleResponse> getSchedulesByClass(JwtPrincipal principal, Long classId);
    List<ScheduleResponse> getMonthlyCalendar(JwtPrincipal principal, Long classId, int year, int month);
}
