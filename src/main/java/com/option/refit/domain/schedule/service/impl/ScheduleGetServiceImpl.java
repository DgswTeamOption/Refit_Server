package com.option.refit.domain.schedule.service.impl;

import com.option.refit.domain.schedule.dto.ScheduleResponse;
import com.option.refit.domain.schedule.repository.ScheduleRepository;
import com.option.refit.domain.schedule.service.ScheduleGetService;
import com.option.refit.global.error.calendarError.exception.InvalidDateFormatException;
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
public class ScheduleGetServiceImpl implements ScheduleGetService {

    private final ScheduleRepository scheduleRepository;

    @Override
    public List<ScheduleResponse> getSchedulesByClass(Long classId) {
        return scheduleRepository.findByClassIdOrderByScheduleTimeAsc(classId).stream()
                .map(ScheduleResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScheduleResponse> getMonthlyCalendar(Long classId, int year, int month) {
        if (year < 1 || year > 9999 || month < 1 || month > 12) {
            throw new InvalidDateFormatException();
        }

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime end = yearMonth.atEndOfMonth().atTime(LocalTime.MAX);

        return scheduleRepository.findMonthlySchedules(classId, start, end).stream()
                .map(ScheduleResponse::new)
                .collect(Collectors.toList());
    }
}
