package com.option.refit.domain.absence.service.impl;

import com.option.refit.domain.absence.entity.Absence;
import com.option.refit.domain.absence.repository.AbsenceRepository;
import com.option.refit.domain.absence.service.AbsenceCancelService;
import com.option.refit.global.error.absenceError.exception.AbsenceAccessDeniedException;
import com.option.refit.global.error.absenceError.exception.AbsenceNotFoundException;
import com.option.refit.global.error.absenceError.exception.AlreadyProcessedAbsenceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AbsenceCancelServiceImpl implements AbsenceCancelService {

    private final AbsenceRepository absenceRepository;

    @Override
    @Transactional
    public void cancelAbsence(Long studentId, Long absenceId) {
        Absence absence = absenceRepository.findById(absenceId)
                .orElseThrow(AbsenceNotFoundException::new);

        // 신청자 본인만 취소 가능
        if (!absence.isOwnedBy(studentId)) {
            throw new AbsenceAccessDeniedException();
        }

        // 승인 전(대기) 상태에서만 취소 가능 (승인/반려/이미 취소된 건 불가)
        if (!absence.isPending()) {
            throw new AlreadyProcessedAbsenceException();
        }

        absence.cancel();
    }
}
