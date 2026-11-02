package com.option.refit.domain.schedule.scheduler;

import com.option.refit.domain.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduleScheduler {

    private final ScheduleRepository scheduleRepository;

    @Scheduled(cron = "0 */10 * * * *")

    public void notifyUpcomingSchedules() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime targetTime = now.plusMinutes(30);

        log.info("[스케줄러 실행] 다가오는 수업 일정 알림 체크 (대상 시각: {})", targetTime);
    }
}
