package com.option.refit.domain.absence.service;

import com.option.refit.domain.absence.dto.AbsenceListResponse;
import com.option.refit.domain.absence.entity.AbsenceStatus;
import com.option.refit.global.jwt.JwtPrincipal;

import java.util.List;

public interface AbsenceListService {

    // 학생: 해당 수업에서 본인이 낸 신청 전체
    List<AbsenceListResponse> getMyAbsences(JwtPrincipal principal, Long classId);

    // 선생님: 담당 수업의 신청 목록 (status, year, month 선택 필터)
    List<AbsenceListResponse> getClassAbsences(
            JwtPrincipal principal, Long classId, AbsenceStatus status, Integer year, Integer month);
}
