package com.option.refit.domain.schedule.service.impl;

import com.option.refit.domain.schedule.entity.ScheduleEntity;
import com.option.refit.domain.schedule.repository.ScheduleRepository;
import com.option.refit.global.error.calendarError.exception.ScheduleNotFoundException;
import com.option.refit.domain.schedule.service.ScheduleDeleteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScheduleDeleteServiceImpl implements ScheduleDeleteService {

    private final ScheduleRepository scheduleRepository;

    @Override
    @Transactional
    public void deleteSchedule(Long classId, Long scheduleId) {
        ScheduleEntity schedule = scheduleRepository.findByIdAndClassId(scheduleId, classId)
                .orElseThrow(() -> new ScheduleNotFoundException());

        scheduleRepository.delete(schedule);
    }
}
