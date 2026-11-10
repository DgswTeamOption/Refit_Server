package com.option.refit.domain.attendance.service.impl;

import com.option.refit.domain.attendance.dto.AttendanceResponse;
import com.option.refit.domain.attendance.entity.Attendance;
import com.option.refit.domain.attendance.entity.AttendanceMethod;
import com.option.refit.domain.attendance.entity.AttendanceStatus;
import com.option.refit.domain.attendance.repository.AttendanceRepository;
import com.option.refit.domain.attendance.service.AttendanceMarkService;
import com.option.refit.domain.classroom.service.ClassRoomAccessValidator;
import com.option.refit.domain.schedule.entity.ScheduleEntity;
import com.option.refit.domain.schedule.repository.ScheduleRepository;
import com.option.refit.global.error.scheduleError.exception.ScheduleNotFoundException;
import com.option.refit.global.jwt.JwtPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AttendanceMarkServiceImpl implements AttendanceMarkService {

    private final AttendanceRepository attendanceRepository;
    private final ScheduleRepository scheduleRepository;
    private final ClassRoomAccessValidator accessValidator;

    @Override
    @Transactional
    public AttendanceResponse markAttendance(
            JwtPrincipal principal, Long classId, Long scheduleId, AttendanceStatus status) {
        // 담당 교사만 처리 가능 (수업이 없으면 404, 담당이 아니면 403)
        accessValidator.requireTeacher(classId, principal.userId());

        // 해당 수업의 일정이어야 함 (404)
        ScheduleEntity schedule = scheduleRepository.findByIdAndClassId(scheduleId, classId)
                .orElseThrow(ScheduleNotFoundException::new);

        // 이미 기록이 있으면 상태 변경, 없으면 새로 기록
        Attendance attendance = attendanceRepository.findByScheduleId(scheduleId)
                .map(existing -> {
                    existing.markByTeacher(status);
                    return existing;
                })
                .orElseGet(() -> attendanceRepository.save(Attendance.builder()
                        .classId(classId)
                        .scheduleId(scheduleId)
                        .studentId(schedule.getStudentId())
                        .attendanceDate(schedule.getStartTime().toLocalDate())
                        .status(status)
                        .method(AttendanceMethod.MANUAL)
                        .build()));

        return new AttendanceResponse(attendance);
    }
}
