package com.option.refit.domain.absence.service.impl;

import com.option.refit.domain.absence.dto.AbsenceListResponse;
import com.option.refit.domain.absence.entity.Absence;
import com.option.refit.domain.absence.entity.AbsenceStatus;
import com.option.refit.domain.absence.repository.AbsenceRepository;
import com.option.refit.domain.absence.service.AbsenceListService;
import com.option.refit.domain.classroom.entity.ClassRoomEntity;
import com.option.refit.domain.classroom.repository.ClassRoomRepository;
import com.option.refit.domain.user.entity.Role;
import com.option.refit.domain.user.entity.User;
import com.option.refit.domain.user.repository.UserRepository;
import com.option.refit.global.error.absenceError.exception.InvalidDateRangeException;
import com.option.refit.global.error.classError.exception.ClassAccessDeniedException;
import com.option.refit.global.error.classError.exception.ClassNotFoundException;
import com.option.refit.global.error.securityError.exception.RoleAccessDeniedException;
import com.option.refit.global.jwt.JwtPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AbsenceListServiceImpl implements AbsenceListService {

    private final AbsenceRepository absenceRepository;
    private final ClassRoomRepository classRoomRepository;
    private final UserRepository userRepository;

    @Override
    public List<AbsenceListResponse> getMyAbsences(JwtPrincipal principal, Long classId) {
        if (principal.role() != Role.STUDENT) {
            throw new RoleAccessDeniedException();
        }
        if (!classRoomRepository.existsById(classId)) {
            throw new ClassNotFoundException();
        }

        // 본인 신청만 조회 (취소 건 포함)
        List<Absence> absences = absenceRepository
                .findByClassIdAndStudentIdOrderByAbsenceDateDesc(classId, principal.userId());
        return toResponses(absences);
    }

    @Override
    public List<AbsenceListResponse> getClassAbsences(
            JwtPrincipal principal, Long classId, AbsenceStatus status, Integer year, Integer month) {
        if (principal.role() != Role.TEACHER) {
            throw new RoleAccessDeniedException();
        }
        ClassRoomEntity classRoom = classRoomRepository.findById(classId)
                .orElseThrow(ClassNotFoundException::new);
        if (!classRoom.getTeacher().getId().equals(principal.userId())) {
            throw new ClassAccessDeniedException();
        }

        // year/month 필터 -> 날짜 범위 (month만 오면 올해로 간주)
        LocalDate from = null;
        LocalDate to = null;
        if (month != null) {
            if (month < 1 || month > 12) {
                throw new InvalidDateRangeException();
            }
            YearMonth ym = YearMonth.of(year != null ? year : LocalDate.now().getYear(), month);
            from = ym.atDay(1);
            to = ym.atEndOfMonth();
        } else if (year != null) {
            from = LocalDate.of(year, 1, 1);
            to = LocalDate.of(year, 12, 31);
        }

        return toResponses(absenceRepository.search(classId, status, from, to));
    }

    // 학생 이름을 한 번에 조회해서 N+1 방지
    private List<AbsenceListResponse> toResponses(List<Absence> absences) {
        Set<Long> studentIds = absences.stream().map(Absence::getStudentId).collect(Collectors.toSet());
        Map<Long, String> names = new HashMap<>();
        userRepository.findAllById(studentIds).forEach(u -> names.put(u.getId(), u.getName()));

        return absences.stream()
                .map(a -> new AbsenceListResponse(a, names.get(a.getStudentId())))
                .toList();
    }
}
