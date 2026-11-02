package com.option.refit.domain.schedule.service.impl;

import com.option.refit.domain.schedule.dto.ScheduleRequest;
import com.option.refit.domain.schedule.entity.ScheduleEntity;
import com.option.refit.domain.schedule.repository.ScheduleRepository;
import com.option.refit.domain.schedule.service.ScheduleUpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScheduleUpdateServiceImpl implements ScheduleUpdateService {

    private final ScheduleRepository scheduleRepository;

    @Override
    @Transactional
    public void updateSchedule(Long classId, Long scheduleId, ScheduleRequest.CreateOrUpdate request) {
        ScheduleEntity schedule = scheduleRepository.findByIdAndClassId(scheduleId, classId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 일정입니다. ID: " + scheduleId));

        schedule.updateSchedule(request.getClassName(), request.getDescription(), request.getScheduleTime());
    }
}
