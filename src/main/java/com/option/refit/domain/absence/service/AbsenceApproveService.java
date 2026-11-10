package com.option.refit.domain.absence.service;

import com.option.refit.domain.absence.dto.AbsenceResponse;
import com.option.refit.global.jwt.JwtPrincipal;

public interface AbsenceApproveService {
    AbsenceResponse approve(JwtPrincipal principal, Long absenceId);

    AbsenceResponse reject(JwtPrincipal principal, Long absenceId);
}
