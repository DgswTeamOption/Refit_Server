package com.option.refit.domain.schedule.repository;

import com.option.refit.domain.schedule.entity.ScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<ScheduleEntity, Long> {

    List<ScheduleEntity> findByClassIdOrderByScheduleTimeAsc(Long classId);

    @Query("SELECT s FROM ScheduleEntity s WHERE s.classId = :classId AND s.scheduleTime BETWEEN :start AND :end ORDER BY s.scheduleTime ASC")
    List<ScheduleEntity> findMonthlySchedules(
            @Param("classId") Long classId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
