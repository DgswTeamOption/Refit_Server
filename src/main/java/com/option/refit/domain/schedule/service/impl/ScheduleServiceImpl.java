package com.option.refit.domain.schedule.service.impl;

import com.option.refit.domain.schedule.dto.ScheduleRequest;
import com.option.refit.domain.schedule.dto.ScheduleResponse;
import com.option.refit.domain.schedule.entity.ScheduleEntity;
import com.option.refit.domain.schedule.repository.ScheduleRepository;
import com.option.refit.domain.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;

    @Override
    public List<ScheduleResponse> getSchedulesByClass(Long classId) {
        return scheduleRepository.findByClassIdOrderByScheduleTimeAsc(classId).stream()
                .map(ScheduleResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScheduleResponse> getMonthlyCalendar(Long classId, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime end = yearMonth.atEndOfMonth().atTime(LocalTime.MAX);

        return scheduleRepository.findMonthlySchedules(classId, start, end).stream()
                .map(ScheduleResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Long createSchedule(Long classId, ScheduleRequest.CreateOrUpdate request){
        ScheduleEntity schedule = ScheduleEntity.builder()
                .classId(classId)
                .className(request.getClassName())
                .description(request.getDescription())
                .scheduleTime(request.getScheduleTime())
                .build();

        return scheduleRepository.save(schedule).getId();
    }

    @Override
    @Transactional
    public void updateSchedule(Long classId, Long scheduleId, ScheduleRequest.CreateOrUpdate request) {
        ScheduleEntity schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 일정입니다. ID: " + scheduleId));

        schedule.updateSchedule(request.getClassName(), request.getDescription(), request.getScheduleTime());
    }

    @Override
    @Transactional
    public void deleteSchedule(Long classId, Long scheduleId) {
        ScheduleEntity schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 일정입니다. ID: " + scheduleId));

        scheduleRepository.delete(schedule);
    }
}
