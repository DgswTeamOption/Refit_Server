package com.option.refit.domain.schedule.repository;

import com.option.refit.domain.schedule.entity.ScheduleRuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRuleRepository extends JpaRepository<ScheduleRuleEntity, Long> {

    List<ScheduleRuleEntity> findByClassId(Long classId);

    List<ScheduleRuleEntity> findByClassIdAndStudentId(Long classId, Long studentId);

    List<ScheduleRuleEntity> findByClassIdAndDayOfWeek(Long classId, DayOfWeek dayOfWeek);

    Optional<ScheduleRuleEntity> findByIdAndClassId(Long id, Long classId);

    // 수업 삭제 시 규칙을 소프트 삭제 (@SQLDelete 적용)
    void deleteAllByClassId(Long classId);
}
