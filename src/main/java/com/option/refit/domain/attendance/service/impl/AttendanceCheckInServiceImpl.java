package com.option.refit.domain.attendance.service.impl;

import com.option.refit.domain.attendance.dto.AttendanceResponse;
import com.option.refit.domain.attendance.entity.Attendance;
import com.option.refit.domain.attendance.entity.AttendanceMethod;
import com.option.refit.domain.attendance.entity.AttendanceStatus;
import com.option.refit.domain.attendance.repository.AttendanceRepository;
import com.option.refit.domain.attendance.service.AttendanceCheckInService;
import com.option.refit.domain.classroom.service.ClassRoomAccessValidator;
import com.option.refit.domain.makeup.entity.Makeup;
import com.option.refit.domain.makeup.entity.MakeupApprovalStatus;
import com.option.refit.domain.makeup.repository.MakeupRepository;
import com.option.refit.domain.schedule.entity.ScheduleEntity;
import com.option.refit.domain.schedule.repository.ScheduleRepository;
import com.option.refit.domain.user.entity.Role;
import com.option.refit.global.error.attendanceError.exception.AbsenceAlreadyRecordedException;
import com.option.refit.global.error.attendanceError.exception.AlreadyAttendedException;
import com.option.refit.global.error.attendanceError.exception.NfcTagScheduleMismatchException;
import com.option.refit.global.error.attendanceError.exception.OutsideAttendanceTimeException;
import com.option.refit.global.error.classError.exception.ClassAccessDeniedException;
import com.option.refit.global.error.scheduleError.exception.ScheduleNotFoundException;
import com.option.refit.global.error.securityError.exception.RoleAccessDeniedException;
import com.option.refit.global.jwt.JwtPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AttendanceCheckInServiceImpl implements AttendanceCheckInService {

    private final AttendanceRepository attendanceRepository;
    private final ScheduleRepository scheduleRepository;
    private final MakeupRepository makeupRepository;
    private final ClassRoomAccessValidator accessValidator;

    // 시작 몇 분 전부터 출석할 수 있는지 (기존 NFC 출석 정책과 같은 기본값 10분)
    @Value("${attendance.nfc.early-minutes:10}")
    private long earlyMinutes;

    // 시작 후 몇 분까지를 정상 출석으로 보는지. 이후 수업 종료까지는 지각
    @Value("${attendance.nfc.late-after-minutes:10}")
    private long lateAfterMinutes;

    @Override
    @Transactional
    public AttendanceResponse checkInByNfc(JwtPrincipal principal, Long classId, Long scheduleId, LocalDateTime now) {
        // 학생만 출석 가능 (403)
        if (principal.role() != Role.STUDENT) {
            throw new RoleAccessDeniedException();
        }
        Long studentId = principal.userId();

        // 수업이 있고(404) 현재 수강 중인 학생이어야 함 (403)
        accessValidator.requireMember(classId, studentId);

        // 존재하는 회차(404)여야 하고 태그에 연결된 수업의 회차(400)여야 함
        ScheduleEntity schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(ScheduleNotFoundException::new);
        if (!schedule.getClassId().equals(classId)) {
            throw new NfcTagScheduleMismatchException();
        }

        // 다른 학생에게 배정된 회차는 출석할 수 없음 (403)
        if (!schedule.getStudentId().equals(studentId)) {
            throw new ClassAccessDeniedException();
        }

        // 이미 출석·지각 처리된 회차면 중복 (409). 같은 요청을 반복해도 기존 기록이 유지되고 새 기록은 만들지 않는다.
        Attendance existing = attendanceRepository.findByScheduleIdAndStudentId(scheduleId, studentId).orElse(null);
        if (existing != null && existing.getStatus() != AttendanceStatus.ABSENT) {
            throw new AlreadyAttendedException();
        }

        // 정규 수업 시간이면 정규 출석, 아니면 승인된 보강 시간인지 확인. 둘 다 아니면 출석 불가 (400)
        AttendanceStatus status = judge(schedule.getStartTime(), schedule.getEndTime(), now);
        boolean makeup = false;
        if (status == null) {
            status = judgeMakeup(studentId, classId, schedule, now);
            makeup = status != null;
        }
        if (status == null) {
            throw new OutsideAttendanceTimeException();
        }

        if (existing != null) {
            // 선생님이 먼저 결석으로 처리한 회차: 정규 출석으로는 바꿀 수 없고(409, 선생님 처리 우선),
            // 승인된 보강 시간에 출석하는 경우에만 결석 기록을 출석(지각)으로 바꾼다.
            if (!makeup) {
                throw new AbsenceAlreadyRecordedException();
            }
            existing.checkInByNfc(status, now);
            return new AttendanceResponse(existing);
        }

        Attendance attendance = Attendance.builder()
                .classId(classId)
                .scheduleId(scheduleId)
                .studentId(studentId)
                .attendanceDate(schedule.getStartTime().toLocalDate())
                .status(status)
                .method(AttendanceMethod.NFC)
                .checkedAt(now)
                .build();

        try {
            return new AttendanceResponse(attendanceRepository.saveAndFlush(attendance));
        } catch (DataIntegrityViolationException e) {
            // 동시에 같은 요청이 들어오면 (schedule_id, student_id) 유니크 제약이 하나만 통과시키고 나머지는 중복(409)
            throw new AlreadyAttendedException();
        }
    }

    // 승인(approval_status=1)된 보강 중 지금 출석 가능한 시간대가 있으면 그 상태를 반환
    private AttendanceStatus judgeMakeup(Long studentId, Long classId, ScheduleEntity schedule, LocalDateTime now) {
        Duration length = Duration.between(schedule.getStartTime(), schedule.getEndTime());
        for (Makeup makeup : makeupRepository.findByStudentIdAndClassIdAndScheduleIdAndApprovalStatus(
                studentId, classId, schedule.getId(), MakeupApprovalStatus.APPROVED)) {
            AttendanceStatus status = judge(makeup.getMakeupTime(), makeup.getMakeupTime().plus(length), now);
            if (status != null) {
                return status;
            }
        }
        return null;
    }

    // 출석 가능 시간 안이면 PRESENT/LATE, 밖이면 null
    private AttendanceStatus judge(LocalDateTime start, LocalDateTime end, LocalDateTime now) {
        if (now.isBefore(start.minusMinutes(earlyMinutes)) || now.isAfter(end)) {
            return null;
        }
        return now.isAfter(start.plusMinutes(lateAfterMinutes)) ? AttendanceStatus.LATE : AttendanceStatus.PRESENT;
    }
}
