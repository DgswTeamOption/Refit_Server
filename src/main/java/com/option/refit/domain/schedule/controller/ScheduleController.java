package com.option.refit.domain.schedule.controller;

import com.option.refit.domain.schedule.dto.ScheduleRequest;
import com.option.refit.domain.schedule.dto.ScheduleResponse;
import com.option.refit.domain.schedule.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/classes/{classId}")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping("/schedule")
    public ResponseEntity<List<ScheduleResponse>> getSchedules(@PathVariable Long classId) {
        return ResponseEntity.ok(scheduleService.getSchedulesByClass(classId));
    }

    @GetMapping("/calendar")
    public ResponseEntity<List<ScheduleResponse>> getMonthlyCalendar(
            @PathVariable Long classId,
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(scheduleService.getMonthlyCalendar(classId, year, month));
    }

    @PostMapping("/schedule")
    public ResponseEntity<Long> createSchedule(
            @PathVariable Long classId,
            @Valid @RequestBody ScheduleRequest.CreateOrUpdate request) {
        Long scheduleId = scheduleService.createSchedule(classId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(scheduleId);
    }

    @PatchMapping("/schedule/{scheduleId}")
    public ResponseEntity<Void> updateSchedule(
            @PathVariable Long classId,
            @PathVariable Long scheduleId,
            @Valid @RequestBody ScheduleRequest.CreateOrUpdate request) {
        scheduleService.updateSchedule(classId, scheduleId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/schedule/{scheduleId}")
    public ResponseEntity<Void> deleteSchedule(
            @PathVariable Long classId,
            @PathVariable Long scheduleId) {
        scheduleService.deleteSchedule(classId, scheduleId);
        return ResponseEntity.noContent().build();
    }
}
