package com.option.refit.domain.schedule.controller;

import com.option.refit.domain.schedule.dto.ScheduleRequest;
import com.option.refit.domain.schedule.dto.ScheduleResponse;
import com.option.refit.domain.schedule.service.ScheduleCreateService;
import com.option.refit.domain.schedule.service.ScheduleDeleteService;
import com.option.refit.domain.schedule.service.ScheduleGetService;
import com.option.refit.domain.schedule.service.ScheduleUpdateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/class")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleGetService scheduleGetService;
    private final ScheduleCreateService scheduleCreateService;
    private final ScheduleUpdateService scheduleUpdateService;
    private final ScheduleDeleteService scheduleDeleteService;

    @GetMapping("/schedule/{classId}")
    public ResponseEntity<List<ScheduleResponse>> getSchedules(@PathVariable Long classId) {
        return ResponseEntity.ok(scheduleGetService.getSchedulesByClass(classId));
    }

    @GetMapping("/calendar/{classId}")
    public ResponseEntity<List<ScheduleResponse>> getMonthlyCalendar(
            @PathVariable Long classId,
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(scheduleGetService.getMonthlyCalendar(classId, year, month));
    }

    @PostMapping("/schedule/{classId}")
    public ResponseEntity<Long> createSchedule(
            @PathVariable Long classId,
            @Valid @RequestBody ScheduleRequest.CreateOrUpdate request) {
        Long scheduleId = scheduleCreateService.createSchedule(classId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(scheduleId);
    }

    @PatchMapping("/schedule/{classId}/{scheduleId}")
    public ResponseEntity<Void> updateSchedule(
            @PathVariable Long classId,
            @PathVariable Long scheduleId,
            @Valid @RequestBody ScheduleRequest.CreateOrUpdate request) {
        scheduleUpdateService.updateSchedule(classId, scheduleId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/schedule/{classId}/{scheduleId}")
    public ResponseEntity<Void> deleteSchedule(
            @PathVariable Long classId,
            @PathVariable Long scheduleId) {
        scheduleDeleteService.deleteSchedule(classId, scheduleId);
        return ResponseEntity.noContent().build();
    }
}
