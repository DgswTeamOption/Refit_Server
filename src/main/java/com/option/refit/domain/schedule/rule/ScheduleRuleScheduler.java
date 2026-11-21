package com.option.refit.domain.schedule.rule;

import com.option.refit.domain.classroom.service.ClassRoomAccessValidator;
import com.option.refit.domain.schedule.entity.ScheduleRuleEntity;
import com.option.refit.domain.schedule.repository.ScheduleRuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

// 매일 새벽 3시에 모든 규칙의 회차를 8주 앞까지 채운다. 서버가 꺼져 있었어도 다음 실행에서 이어서 채운다.
@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduleRuleScheduler {

    private final ScheduleRuleRepository ruleRepository;
    private final ScheduleRuleGenerator generator;
    private final ClassRoomAccessValidator accessValidator;

    @Scheduled(cron = "0 0 3 * * *")
    public void extendRules() {
        for (ScheduleRuleEntity rule : ruleRepository.findAll()) {
            try {
                // 수업을 나간 학생의 규칙은 더 이상 회차를 만들지 않는다
                if (accessValidator.isActiveMember(rule.getClassId(), rule.getStudentId())) {
                    generator.generate(rule);
                }
            } catch (Exception e) {
                log.warn("[반복 일정 확장 실패] ruleId={}", rule.getId(), e);
            }
        }
    }
}
