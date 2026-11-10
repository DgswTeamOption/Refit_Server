package com.option.refit.domain.attendance.repository;

import com.option.refit.domain.attendance.entity.Attendance;
import com.option.refit.domain.attendance.entity.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByScheduleId(Long scheduleId);

    boolean existsByScheduleId(Long scheduleId);

    // 여러 일정의 출석 기록을 한 번에 조회
    List<Attendance> findByScheduleIdIn(Collection<Long> scheduleIds);

    // 학생 본인의 기간별 출석 내역
    List<Attendance> findByClassIdAndStudentIdAndAttendanceDateBetweenOrderByAttendanceDateAscIdAsc(
            Long classId, Long studentId, LocalDate from, LocalDate to);

    // 기간 내 학생별·상태별 건수 집계 (studentId가 null이면 수업 전체 학생)
    @Query("SELECT a.studentId AS studentId, a.status AS status, COUNT(a) AS count " +
            "FROM Attendance a " +
            "WHERE a.classId = :classId AND a.attendanceDate BETWEEN :from AND :to " +
            "AND (:studentId IS NULL OR a.studentId = :studentId) " +
            "GROUP BY a.studentId, a.status")
    List<StatusCount> countByStudentAndStatus(
            @Param("classId") Long classId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("studentId") Long studentId
    );

    // 학생·상태별 집계 결과 프로젝션
    interface StatusCount {
        Long getStudentId();

        AttendanceStatus getStatus();

        long getCount();
    }
}
