package com.option.refit.domain.schedule.service.impl;

import com.option.refit.domain.classroom.service.ClassRoomAccessValidator;
import com.option.refit.domain.schedule.dto.ScheduleResponse;
import com.option.refit.domain.schedule.entity.ScheduleEntity;
import com.option.refit.domain.schedule.repository.ScheduleRepository;
import com.option.refit.domain.schedule.service.ScheduleGetService;
import com.option.refit.domain.user.entity.Role;
import com.option.refit.global.error.scheduleError.exception.InvalidDateFormatException;
import com.option.refit.global.jwt.JwtPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleGetServiceImpl implements ScheduleGetService {

    private final ScheduleRepository scheduleRepository;
    private final ClassRoomAccessValidator accessValidator;

    @Override
    public List<ScheduleResponse> getSchedulesByClass(JwtPrincipal principal, Long classId) {
        accessValidator.requireTeacherOrMember(classId, principal.userId());

        // 학생은 본인 일정만, 담당 교사는 수업 전체 일정 조회
        List<ScheduleEntity> schedules = principal.role() == Role.STUDENT
                ? scheduleRepository.findByClassIdAndStudentIdOrderByStartTimeAsc(classId, principal.userId())
                : scheduleRepository.findByClassIdOrderByStartTimeAsc(classId);

        return schedules.stream()
                .map(ScheduleResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScheduleResponse> getMonthlyCalendar(JwtPrincipal principal, Long classId, int year, int month) {
        accessValidator.requireTeacherOrMember(classId, principal.userId());

        if (year < 1 || year > 9999 || month < 1 || month > 12) {
            throw new InvalidDateFormatException();
        }

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime end = yearMonth.atEndOfMonth().atTime(LocalTime.MAX);

        return scheduleRepository.findMonthlySchedules(classId, visibleStudentId(principal), start, end).stream()
                .map(ScheduleResponse::new)
                .collect(Collectors.toList());
    }

    // 학생은 본인 일정만, 담당 교사는 전체(null = 필터 없음)
    private Long visibleStudentId(JwtPrincipal principal) {
        return principal.role() == Role.STUDENT ? principal.userId() : null;
    }
}
