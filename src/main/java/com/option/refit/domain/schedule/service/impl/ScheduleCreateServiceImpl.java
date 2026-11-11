package com.option.refit.domain.schedule.service.impl;

import com.option.refit.domain.classroom.service.ClassRoomAccessValidator;
import com.option.refit.domain.schedule.dto.ScheduleRequest;
import com.option.refit.domain.schedule.entity.ScheduleEntity;
import com.option.refit.domain.schedule.repository.ScheduleRepository;
import com.option.refit.domain.schedule.service.ScheduleCreateService;
import com.option.refit.global.error.scheduleError.exception.InvalidDateFormatException;
import com.option.refit.global.error.scheduleError.exception.StudentNotInClassException;
import com.option.refit.global.jwt.JwtPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScheduleCreateServiceImpl implements ScheduleCreateService {

    private final ScheduleRepository scheduleRepository;
    private final ClassRoomAccessValidator accessValidator;

    @Override
    @Transactional
    public Long createSchedule(JwtPrincipal principal, Long classId, ScheduleRequest.Create request) {
        // 해당 수업의 담당 교사만 등록 가능
        accessValidator.requireTeacher(classId, principal.userId());

        // 선택한 학생이 해당 수업에 현재 참여 중이어야 함
        if (!accessValidator.isActiveMember(classId, request.getStudentId())) {
            throw new StudentNotInClassException();
        }

        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new InvalidDateFormatException();
        }

        ScheduleEntity schedule = ScheduleEntity.builder()
                .classId(classId)
                .studentId(request.getStudentId())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .build();

        return scheduleRepository.save(schedule).getId();
    }
}
