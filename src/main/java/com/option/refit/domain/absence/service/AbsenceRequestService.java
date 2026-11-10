package com.option.refit.domain.absence.service;

import com.option.refit.domain.absence.dto.AbsenceRequest;
import com.option.refit.domain.absence.dto.AbsenceResponse;

public interface AbsenceRequestService {
    AbsenceResponse requestAbsence(Long studentId, Long classId, AbsenceRequest request);
}
