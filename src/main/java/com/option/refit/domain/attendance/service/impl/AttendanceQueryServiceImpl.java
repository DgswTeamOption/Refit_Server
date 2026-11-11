package com.option.refit.domain.attendance.service.impl;

import com.option.refit.domain.attendance.dto.AttendanceResponse;
import com.option.refit.domain.attendance.dto.AttendanceStatusResponse;
import com.option.refit.domain.attendance.dto.AttendanceSummaryResponse;
import com.option.refit.domain.attendance.dto.AttendanceUncheckedResponse;
import com.option.refit.domain.attendance.entity.Attendance;
import com.option.refit.domain.attendance.entity.AttendanceStatus;
import com.option.refit.domain.attendance.repository.AttendanceRepository;
import com.option.refit.domain.attendance.repository.AttendanceRepository.StatusCount;
import com.option.refit.domain.attendance.service.AttendanceQueryService;
import com.option.refit.domain.classroom.entity.ClassRoomMemberEntity;
import com.option.refit.domain.classroom.repository.ClassRoomMemberRepository;
import com.option.refit.domain.classroom.service.ClassRoomAccessValidator;
import com.option.refit.domain.schedule.entity.ScheduleEntity;
import com.option.refit.domain.schedule.repository.ScheduleRepository;
import com.option.refit.domain.user.entity.Role;
import com.option.refit.domain.user.repository.UserRepository;
import com.option.refit.global.error.scheduleError.exception.InvalidDateFormatException;
import com.option.refit.global.error.securityError.exception.RoleAccessDeniedException;
import com.option.refit.global.jwt.JwtPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttendanceQueryServiceImpl implements AttendanceQueryService {

    private final AttendanceRepository attendanceRepository;
    private final ScheduleRepository scheduleRepository;
    private final ClassRoomAccessValidator accessValidator;
    private final UserRepository userRepository;
    private final ClassRoomMemberRepository memberRepository;

    @Override
    public List<AttendanceResponse> getMyAttendances(JwtPrincipal principal, Long classId, int year, int month) {
        if (principal.role() != Role.STUDENT) {
            throw new RoleAccessDeniedException();
        }
        accessValidator.requireMember(classId, principal.userId());

        YearMonth yearMonth = toYearMonth(year, month);

        return attendanceRepository
                .findByClassIdAndStudentIdAndAttendanceDateBetweenOrderByAttendanceDateAscIdAsc(
                        classId, principal.userId(), yearMonth.atDay(1), yearMonth.atEndOfMonth())
                .stream()
                .map(AttendanceResponse::new)
                .toList();
    }

    @Override
    public List<AttendanceStatusResponse> getClassAttendances(JwtPrincipal principal, Long classId, LocalDate date) {
        requireTeacherRole(principal);
        accessValidator.requireTeacher(classId, principal.userId());

        // 해당 날짜의 수업 일정 전체
        List<ScheduleEntity> schedules = scheduleRepository.findMonthlySchedules(
                classId, null, date.atStartOfDay(), date.atTime(LocalTime.MAX));
        if (schedules.isEmpty()) {
            return List.of();
        }

        // 출석 기록과 학생 이름을 각각 한 번에 조회해 N+1 방지
        Map<Long, Attendance> attendanceBySchedule = attendanceRepository
                .findByScheduleIdIn(schedules.stream().map(ScheduleEntity::getId).toList())
                .stream()
                .collect(Collectors.toMap(Attendance::getScheduleId, Function.identity()));
        Map<Long, String> names = findNames(schedules.stream().map(ScheduleEntity::getStudentId).toList());

        return schedules.stream()
                .map(schedule -> new AttendanceStatusResponse(
                        schedule,
                        names.get(schedule.getStudentId()),
                        attendanceBySchedule.get(schedule.getId())))
                .toList();
    }

    @Override
    public List<AttendanceUncheckedResponse> getUncheckedStudents(JwtPrincipal principal, Long classId, LocalDate date) {
        requireTeacherRole(principal);
        accessValidator.requireTeacher(classId, principal.userId());

        // 그 날짜에 출석·지각으로 기록된 학생
        List<Attendance> attendances = attendanceRepository.findByClassIdAndAttendanceDate(classId, date);
        Set<Long> attended = new HashSet<>();
        Set<Long> absent = new HashSet<>();
        for (Attendance attendance : attendances) {
            (attendance.getStatus() == AttendanceStatus.ABSENT ? absent : attended).add(attendance.getStudentId());
        }

        // 그 날짜에 일정이 잡힌 학생 (일정이 없는 수강생은 출석 대상이 아닐 수 있어 구분해서 내려준다)
        Set<Long> scheduled = new HashSet<>();
        scheduleRepository.findMonthlySchedules(classId, null, date.atStartOfDay(), date.atTime(LocalTime.MAX))
                .forEach(schedule -> scheduled.add(schedule.getStudentId()));

        // 현재 수강생 명단(left_at IS NULL)에서 출석·지각 기록이 없는 학생
        return memberRepository.findAllByClassroom_IdAndLeftAtIsNull(classId).stream()
                .map(ClassRoomMemberEntity::getUser)
                .filter(user -> !attended.contains(user.getId()))
                .map(user -> new AttendanceUncheckedResponse(
                        user.getId(),
                        user.getName(),
                        absent.contains(user.getId()) ? AttendanceStatus.ABSENT.name() : AttendanceStatusResponse.NOT_CHECKED,
                        scheduled.contains(user.getId())))
                .toList();
    }

    @Override
    public List<AttendanceSummaryResponse> getMonthlySummary(
            JwtPrincipal principal, Long classId, int year, int month) {
        boolean isStudent = principal.role() == Role.STUDENT;

        if (isStudent) {
            accessValidator.requireMember(classId, principal.userId());
        } else {
            accessValidator.requireTeacher(classId, principal.userId());
        }

        YearMonth yearMonth = toYearMonth(year, month);
        Long onlyStudentId = isStudent ? principal.userId() : null;

        // 학생별·상태별 건수
        Map<Long, Map<AttendanceStatus, Long>> counts = new HashMap<>();
        for (StatusCount row : attendanceRepository.countByStudentAndStatus(
                classId, yearMonth.atDay(1), yearMonth.atEndOfMonth(), onlyStudentId)) {
            counts.computeIfAbsent(row.getStudentId(), id -> new HashMap<>())
                    .put(row.getStatus(), row.getCount());
        }

        // 기록이 없는 학생도 0건으로 보이도록, 해당 월에 일정이 잡힌 학생을 함께 포함
        Set<Long> studentIds = new TreeSet<>(counts.keySet());
        scheduleRepository.findMonthlySchedules(
                        classId, onlyStudentId, yearMonth.atDay(1).atStartOfDay(),
                        yearMonth.atEndOfMonth().atTime(LocalTime.MAX))
                .forEach(schedule -> studentIds.add(schedule.getStudentId()));

        Map<Long, String> names = findNames(studentIds);

        return studentIds.stream()
                .map(studentId -> {
                    Map<AttendanceStatus, Long> byStatus = counts.getOrDefault(studentId, Map.of());
                    return new AttendanceSummaryResponse(
                            studentId,
                            names.get(studentId),
                            yearMonth.getYear(),
                            yearMonth.getMonthValue(),
                            byStatus.getOrDefault(AttendanceStatus.PRESENT, 0L),
                            byStatus.getOrDefault(AttendanceStatus.LATE, 0L),
                            byStatus.getOrDefault(AttendanceStatus.ABSENT, 0L));
                })
                .toList();
    }

    private void requireTeacherRole(JwtPrincipal principal) {
        if (principal.role() != Role.TEACHER) {
            throw new RoleAccessDeniedException();
        }
    }

    private YearMonth toYearMonth(int year, int month) {
        if (year < 1 || year > 9999 || month < 1 || month > 12) {
            throw new InvalidDateFormatException();
        }
        return YearMonth.of(year, month);
    }

    // 학생 이름을 한 번에 조회
    private Map<Long, String> findNames(Collection<Long> studentIds) {
        Map<Long, String> names = new HashMap<>();
        userRepository.findAllById(Set.copyOf(studentIds)).forEach(u -> names.put(u.getId(), u.getName()));
        return names;
    }
}
