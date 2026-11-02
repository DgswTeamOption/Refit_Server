package com.option.refit.domain.schedule.service.impl;

import com.option.refit.domain.schedule.dto.ScheduleRequest;
import com.option.refit.domain.schedule.entity.ScheduleEntity;
import com.option.refit.domain.schedule.repository.ScheduleRepository;
import com.option.refit.domain.schedule.service.ScheduleCreateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScheduleCreateServiceImpl implements ScheduleCreateService {

    private final ScheduleRepository scheduleRepository;

    @Override
    @Transactional
    public Long createSchedule(Long classId, ScheduleRequest.CreateOrUpdate request) {
        ScheduleEntity schedule = ScheduleEntity.builder()
                .classId(classId)
                .className(request.getClassName())
                .description(request.getDescription())
                .scheduleTime(request.getScheduleTime())
                .build();

        return scheduleRepository.save(schedule).getId();
    }
}
