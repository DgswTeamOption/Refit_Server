package com.option.refit.domain.schedule.repository;

import com.option.refit.domain.schedule.entity.ScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<ScheduleEntity, Long> {

    List<ScheduleEntity> findByClassIdOrderByStartTimeAsc(Long classId);

    // 학생 본인의 일정만 조회
    List<ScheduleEntity> findByClassIdAndStudentIdOrderByStartTimeAsc(Long classId, Long studentId);

    Optional<ScheduleEntity> findByIdAndClassId(Long id, Long classId);

    @Query("SELECT s FROM ScheduleEntity s WHERE s.classId = :classId AND s.startTime BETWEEN :start AND :end AND (:studentId IS NULL OR s.studentId = :studentId) ORDER BY s.startTime ASC")
    List<ScheduleEntity> findMonthlySchedules(
            @Param("classId") Long classId,
            @Param("studentId") Long studentId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    // 출석 가능한 일정 조회: 아직 끝나지 않았고(endTime >= now), 시작 허용 시각(limit) 이전에 시작하는 일정
    @Query("SELECT s FROM ScheduleEntity s WHERE s.classId = :classId AND s.studentId = :studentId " +
            "AND s.endTime >= :now AND s.startTime <= :limit ORDER BY s.startTime ASC")
    List<ScheduleEntity> findAttendable(
            @Param("classId") Long classId,
            @Param("studentId") Long studentId,
            @Param("now") LocalDateTime now,
            @Param("limit") LocalDateTime limit
    );

    // 수업 삭제 시 회차를 소프트 삭제 (@SQLDelete 적용)
    void deleteAllByClassId(Long classId);
}
