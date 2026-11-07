package com.option.refit.domain.schedule.service.impl;

import com.option.refit.domain.schedule.dto.ScheduleRequest;
import com.option.refit.domain.schedule.entity.ScheduleEntity;
import com.option.refit.domain.schedule.repository.ScheduleRepository;
import com.option.refit.domain.schedule.service.ScheduleCreateService;
import com.option.refit.global.error.scheduleError.exception.InvalidDateFormatException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScheduleCreateServiceImpl implements ScheduleCreateService {

    private final ScheduleRepository scheduleRepository;

    @Override
    @Transactional
    public Long createSchedule(Long classId, ScheduleRequest.Create request) {
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
