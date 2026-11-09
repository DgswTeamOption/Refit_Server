package com.option.refit.domain.schedule.service.impl;

import com.option.refit.domain.schedule.dto.ScheduleRequest;
import com.option.refit.domain.schedule.entity.ScheduleEntity;
import com.option.refit.domain.schedule.repository.ScheduleRepository;
import com.option.refit.global.error.scheduleError.exception.ScheduleNotFoundException;
import com.option.refit.domain.schedule.service.ScheduleUpdateService;
import com.option.refit.global.error.scheduleError.exception.InvalidDateFormatException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScheduleUpdateServiceImpl implements ScheduleUpdateService {

    private final ScheduleRepository scheduleRepository;

    @Override
    @Transactional
    public void updateSchedule(Long classId, Long scheduleId, ScheduleRequest.Update request) {
        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new InvalidDateFormatException();
        }

        ScheduleEntity schedule = scheduleRepository.findByIdAndClassId(scheduleId, classId)
                .orElseThrow(() -> new ScheduleNotFoundException());

        schedule.updateTime(request.getStartTime(), request.getEndTime());
    }
}
