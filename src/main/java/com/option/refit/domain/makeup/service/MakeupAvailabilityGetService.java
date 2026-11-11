package com.option.refit.domain.makeup.service;

import com.option.refit.domain.makeup.dto.MakeupAvailabilityResponse;
import com.option.refit.global.jwt.JwtPrincipal;

import java.util.List;

public interface MakeupAvailabilityGetService {

    // 수업별(classId) 또는 선생님별(teacherId) 보강 가능 시간 목록. 둘 다 주면 교집합, 둘 다 없으면 400.
    // scheduleId 는 선택 필터. 담당 선생님 또는 참여 중인 학생이 속한 수업의 데이터만 조회된다.
    List<MakeupAvailabilityResponse> getAvailabilities(
            JwtPrincipal principal, Long classId, Long teacherId, Long scheduleId);
}
