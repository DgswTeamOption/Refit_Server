package com.option.refit.domain.attendance.service.impl;

import com.option.refit.domain.attendance.dto.AttendanceResponse;
import com.option.refit.domain.attendance.entity.Attendance;
import com.option.refit.domain.attendance.entity.AttendanceMethod;
import com.option.refit.domain.attendance.entity.AttendanceStatus;
import com.option.refit.domain.attendance.repository.AttendanceRepository;
import com.option.refit.domain.attendance.service.NfcAttendanceService;
import com.option.refit.domain.classroom.service.ClassRoomAccessValidator;
import com.option.refit.domain.schedule.entity.ScheduleEntity;
import com.option.refit.domain.schedule.repository.ScheduleRepository;
import com.option.refit.domain.user.entity.Role;
import com.option.refit.global.error.attendanceError.exception.AlreadyAttendedException;
import com.option.refit.global.error.attendanceError.exception.OutsideAttendanceTimeException;
import com.option.refit.global.error.securityError.exception.RoleAccessDeniedException;
import com.option.refit.global.jwt.JwtPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NfcAttendanceServiceImpl implements NfcAttendanceService {

    // 수업 시작 몇 분 전부터 출석할 수 있는지 (수업 종료 시각까지 가능)
    static final long CHECK_IN_EARLY_MINUTES = 10;

    private final AttendanceRepository attendanceRepository;
    private final ScheduleRepository scheduleRepository;
    private final ClassRoomAccessValidator accessValidator;
    private final NfcTags nfcTags;

    @Override
    @Transactional
    public AttendanceResponse checkIn(JwtPrincipal principal, Long classId, String tagUid) {
        // 학생만 NFC 출석 가능 (403)
        if (principal.role() != Role.STUDENT) {
            throw new RoleAccessDeniedException();
        }

        // 수업이 존재하고(404) 해당 수업에 참여 중인 학생이어야 함(403)
        accessValidator.requireMember(classId, principal.userId());

        // 등록된 태그(404)이고 이 수업의 태그(400)여야 함
        nfcTags.getTagOfClass(classId, tagUid);

        // 지금 출석 가능한 본인 일정이 있어야 함 (400)
        LocalDateTime now = LocalDateTime.now();
        List<ScheduleEntity> candidates = scheduleRepository.findAttendable(
                classId, principal.userId(), now, now.plusMinutes(CHECK_IN_EARLY_MINUTES));
        if (candidates.isEmpty()) {
            throw new OutsideAttendanceTimeException();
        }
        ScheduleEntity schedule = candidates.get(0);

        // 이미 출석·결석 처리된 일정이면 중복 (409)
        if (attendanceRepository.existsByScheduleId(schedule.getId())) {
            throw new AlreadyAttendedException();
        }

        Attendance attendance = Attendance.builder()
                .classId(classId)
                .scheduleId(schedule.getId())
                .studentId(principal.userId())
                .attendanceDate(schedule.getStartTime().toLocalDate())
                .status(AttendanceStatus.PRESENT)
                .method(AttendanceMethod.NFC)
                .build();

        try {
            return new AttendanceResponse(attendanceRepository.saveAndFlush(attendance));
        } catch (DataIntegrityViolationException e) {
            // 같은 태그를 동시에 여러 번 찍은 경우 유니크 제약으로 걸러짐
            throw new AlreadyAttendedException();
        }
    }
}
