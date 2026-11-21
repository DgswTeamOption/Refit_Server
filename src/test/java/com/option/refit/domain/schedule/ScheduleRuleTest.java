package com.option.refit.domain.schedule;

import com.option.refit.domain.schedule.entity.ScheduleEntity;
import com.option.refit.domain.schedule.entity.ScheduleRuleEntity;
import com.option.refit.domain.schedule.repository.ScheduleRepository;
import com.option.refit.domain.schedule.repository.ScheduleRuleRepository;
import com.option.refit.domain.schedule.rule.ScheduleRuleGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:schedule-rule-test;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Import(ScheduleRuleGenerator.class)
class ScheduleRuleTest {

    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private ScheduleRuleRepository ruleRepository;
    @Autowired
    private ScheduleRuleGenerator generator;

    @Test
    void generateCreatesFutureSessionsOnRuleWeekdayAndIsIdempotent() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        ScheduleRuleEntity rule = ruleRepository.saveAndFlush(ScheduleRuleEntity.builder()
                .classId(1L).studentId(10L).dayOfWeek(tomorrow.getDayOfWeek())
                .startTime(LocalTime.of(16, 0)).endTime(LocalTime.of(17, 0)).build());

        generator.generate(rule);
        generator.generate(rule); // 다시 호출해도 중복 생성되지 않는다

        List<ScheduleEntity> sessions = scheduleRepository.findByClassIdOrderByStartTimeAsc(1L);
        assertEquals(8, sessions.size()); // 오늘부터 8주 뒤까지 내일 요일은 8번
        assertTrue(sessions.stream().allMatch(s -> rule.getId().equals(s.getRuleId())
                && s.getStartTime().getDayOfWeek() == rule.getDayOfWeek()
                && s.getStartTime().isAfter(LocalDateTime.now())));
    }

    @Test
    void deleteFutureRemovesOnlyUpcomingSessions() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        ScheduleRuleEntity rule = ruleRepository.saveAndFlush(ScheduleRuleEntity.builder()
                .classId(2L).studentId(10L).dayOfWeek(tomorrow.getDayOfWeek())
                .startTime(LocalTime.of(16, 0)).endTime(LocalTime.of(17, 0)).build());
        scheduleRepository.saveAndFlush(ScheduleEntity.builder().classId(2L).studentId(10L)
                .startTime(LocalDate.now().minusDays(7).atTime(16, 0))
                .endTime(LocalDate.now().minusDays(7).atTime(17, 0)).ruleId(rule.getId()).build());
        generator.generate(rule);

        generator.deleteFuture(rule.getId());
        scheduleRepository.flush();

        List<ScheduleEntity> left = scheduleRepository.findByClassIdOrderByStartTimeAsc(2L);
        assertEquals(1, left.size()); // 지난 회차만 남는다
        assertTrue(left.get(0).getStartTime().isBefore(LocalDateTime.now()));
    }
}
