package com.option.refit.domain.schedule.service.impl;

import com.option.refit.domain.schedule.entity.ScheduleEntity;
import com.option.refit.domain.schedule.repository.ScheduleRepository;
import com.option.refit.global.error.scheduleError.exception.ScheduleNotFoundException;
import com.option.refit.domain.schedule.service.ScheduleDeleteService;
import com.option.refit.domain.classroom.service.ClassRoomAccessValidator;
import com.option.refit.global.jwt.JwtPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScheduleDeleteServiceImpl implements ScheduleDeleteService {

    private final ScheduleRepository scheduleRepository;
    private final ClassRoomAccessValidator accessValidator;

    @Override
    @Transactional
    public void deleteSchedule(JwtPrincipal principal, Long classId, Long scheduleId) {
        // 해당 수업의 담당 교사만 가능
        accessValidator.requireTeacher(classId, principal.userId());

        ScheduleEntity schedule = scheduleRepository.findByIdAndClassId(scheduleId, classId)
                .orElseThrow(() -> new ScheduleNotFoundException());

        scheduleRepository.delete(schedule);
    }
}
