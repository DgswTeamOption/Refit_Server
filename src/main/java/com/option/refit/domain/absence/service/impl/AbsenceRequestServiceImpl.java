package com.option.refit.domain.absence.service.impl;

import com.option.refit.domain.absence.dto.AbsenceRequest;
import com.option.refit.domain.absence.dto.AbsenceResponse;
import com.option.refit.domain.absence.entity.Absence;
import com.option.refit.domain.absence.entity.AbsenceStatus;
import com.option.refit.domain.absence.repository.AbsenceRepository;
import com.option.refit.domain.absence.service.AbsenceRequestService;
import com.option.refit.global.error.absenceError.exception.DuplicateAbsenceException;
import com.option.refit.global.error.absenceError.exception.InvalidDateRangeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AbsenceRequestServiceImpl implements AbsenceRequestService {

    private final AbsenceRepository absenceRepository;

    @Override
    @Transactional
    public AbsenceResponse requestAbsence(Long studentId, Long classId, AbsenceRequest request) {
        // 지난 날짜로는 신청 불가 (오늘은 허용)
        if (request.getAbsenceDate().isBefore(LocalDate.now())) {
            throw new InvalidDateRangeException();
        }

        // 취소되지 않은 동일 신청이 있으면 중복
        if (absenceRepository.existsByStudentIdAndClassIdAndAbsenceDateAndStatusNot(
                studentId, classId, request.getAbsenceDate(), AbsenceStatus.CANCELED)) {
            throw new DuplicateAbsenceException();
        }

        Absence absence = Absence.builder()
                .studentId(studentId)
                .classId(classId)
                .absenceDate(request.getAbsenceDate())
                .reason(request.getReason())
                .build();

        return new AbsenceResponse(absenceRepository.save(absence));
    }
}
