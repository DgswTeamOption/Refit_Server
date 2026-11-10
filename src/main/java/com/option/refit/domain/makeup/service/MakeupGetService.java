package com.option.refit.domain.makeup.service;

import com.option.refit.domain.makeup.dto.MakeupResponse;
import com.option.refit.domain.makeup.dto.MakeupScheduleResponse;
import com.option.refit.global.jwt.JwtPrincipal;

import java.util.List;

public interface MakeupGetService {

    // 해당 수업의 보강 신청 목록 (approval_status로 필터링, 생략 시 전체)
    List<MakeupResponse> getMakeups(JwtPrincipal principal, Long classId, Integer approvalStatus);

    // 해당 수업의 보강 일정 (student_id / schedule_id / makeup_time으로 필터링, 모두 생략 가능)
    List<MakeupScheduleResponse> getMakeupSchedules(
            JwtPrincipal principal, Long classId, Long studentId, Long scheduleId, String makeupTime);
}
