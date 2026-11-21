package com.option.refit.domain.schedule.rule;

import com.option.refit.domain.schedule.entity.ScheduleEntity;
import com.option.refit.domain.schedule.entity.ScheduleRuleEntity;
import com.option.refit.domain.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

// 요일 규칙에서 회차(schedules)를 만들고 지운다.
@Component
@RequiredArgsConstructor
public class ScheduleRuleGenerator {

    static final int HORIZON_WEEKS = 8;

    private final ScheduleRepository scheduleRepository;

    // 오늘부터 8주 뒤까지 규칙 요일의 회차를 만든다.
    // 이미 있는 회차와 이미 시작한 시각은 건너뛰므로 여러 번 호출해도 안전하다.
    @Transactional
    public void generate(ScheduleRuleEntity rule) {
        LocalDateTime now = LocalDateTime.now();
        LocalDate last = LocalDate.now().plusWeeks(HORIZON_WEEKS);

        for (LocalDate date = LocalDate.now(); !date.isAfter(last); date = date.plusDays(1)) {
            if (date.getDayOfWeek() != rule.getDayOfWeek()) {
                continue;
            }
            LocalDateTime start = date.atTime(rule.getStartTime());
            if (!start.isAfter(now) || scheduleRepository.existsByRuleIdAndStartTime(rule.getId(), start)) {
                continue;
            }
            scheduleRepository.save(ScheduleEntity.builder()
                    .classId(rule.getClassId())
                    .studentId(rule.getStudentId())
                    .startTime(start)
                    .endTime(date.atTime(rule.getEndTime()))
                    .ruleId(rule.getId())
                    .build());
        }
    }

    // 아직 시작 전인 회차만 지운다. 지난 회차와 출석 기록은 보존한다.
    @Transactional
    public void deleteFuture(Long ruleId) {
        scheduleRepository.deleteAll(
                scheduleRepository.findByRuleIdAndStartTimeGreaterThanEqual(ruleId, LocalDateTime.now()));
    }
}
