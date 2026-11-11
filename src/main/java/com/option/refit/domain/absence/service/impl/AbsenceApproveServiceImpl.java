package com.option.refit.domain.absence.service.impl;

import com.option.refit.domain.absence.dto.AbsenceResponse;
import com.option.refit.domain.absence.entity.Absence;
import com.option.refit.domain.absence.repository.AbsenceRepository;
import com.option.refit.domain.absence.service.AbsenceApproveService;
import com.option.refit.domain.classroom.service.ClassRoomAccessValidator;
import com.option.refit.domain.user.entity.Role;
import com.option.refit.global.error.absenceError.exception.AbsenceNotFoundException;
import com.option.refit.global.error.absenceError.exception.AlreadyProcessedAbsenceException;
import com.option.refit.global.error.securityError.exception.RoleAccessDeniedException;
import com.option.refit.global.jwt.JwtPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AbsenceApproveServiceImpl implements AbsenceApproveService {

    private final AbsenceRepository absenceRepository;
    private final ClassRoomAccessValidator accessValidator;

    @Override
    @Transactional
    public AbsenceResponse approve(JwtPrincipal principal, Long absenceId) {
        Absence absence = getProcessableAbsence(principal, absenceId);
        absence.approve();
        return new AbsenceResponse(absence);
    }

    @Override
    @Transactional
    public AbsenceResponse reject(JwtPrincipal principal, Long absenceId) {
        Absence absence = getProcessableAbsence(principal, absenceId);
        absence.reject();
        return new AbsenceResponse(absence);
    }

    // 선생님 권한 확인 + 본인이 담당하는 수업의 신청 건만 + 대기 상태인 신청만 처리 가능
    private Absence getProcessableAbsence(JwtPrincipal principal, Long absenceId) {
        if (principal.role() != Role.TEACHER) {
            throw new RoleAccessDeniedException();
        }

        Absence absence = absenceRepository.findById(absenceId)
                .orElseThrow(AbsenceNotFoundException::new);

        // 담당 교사가 아니면 403 (이미 처리된 건인지 알려주기 전에 권한부터 확인)
        accessValidator.requireTeacher(absence.getClassId(), principal.userId());

        if (!absence.isPending()) {
            throw new AlreadyProcessedAbsenceException();
        }
        return absence;
    }
}
