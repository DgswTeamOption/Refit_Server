package com.option.refit.domain.makeup.repository;

import com.option.refit.domain.makeup.entity.Makeup;
import com.option.refit.domain.makeup.entity.MakeupApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface MakeupRepository extends JpaRepository<Makeup, Long> {

    // 시간대별 정원 체크: 거절되지 않은(대기+승인) 신청 수
    long countByClassIdAndScheduleIdAndMakeupTimeAndApprovalStatusNot(
            Long classId, Long scheduleId, LocalDateTime makeupTime, MakeupApprovalStatus status);

    // 같은 학생이 같은 수업 일정에 거절되지 않은 보강 신청을 이미 했는지 (중복 신청 방지)
    boolean existsByStudentIdAndClassIdAndScheduleIdAndApprovalStatusNot(
            Long studentId, Long classId, Long scheduleId, MakeupApprovalStatus status);

    // 보강 목록 조회: 승인 상태(선택), 학생(선택, 학생 본인 조회용)으로 필터링
    @Query("SELECT m FROM Makeup m WHERE m.classId = :classId " +
            "AND (:status IS NULL OR m.approvalStatus = :status) " +
            "AND (:studentId IS NULL OR m.studentId = :studentId) " +
            "ORDER BY m.createdAt DESC")
    List<Makeup> findList(
            @Param("classId") Long classId,
            @Param("status") MakeupApprovalStatus status,
            @Param("studentId") Long studentId
    );

    // 보강 일정 조회: student_id / schedule_id / makeup_time(모두 선택)으로 필터링
    @Query("SELECT m FROM Makeup m WHERE m.classId = :classId " +
            "AND (:studentId IS NULL OR m.studentId = :studentId) " +
            "AND (:scheduleId IS NULL OR m.scheduleId = :scheduleId) " +
            "AND (:makeupTime IS NULL OR m.makeupTime = :makeupTime) " +
            "ORDER BY m.makeupTime ASC")
    List<Makeup> findSchedules(
            @Param("classId") Long classId,
            @Param("studentId") Long studentId,
            @Param("scheduleId") Long scheduleId,
            @Param("makeupTime") LocalDateTime makeupTime
    );

    // 수업 내 학생별 보강 횟수 집계 (상태 필터, 학생 목록 한 번에 조회)
    @Query("SELECT m.studentId AS studentId, COUNT(m) AS makeupCount " +
            "FROM Makeup m " +
            "WHERE m.classId = :classId AND m.approvalStatus = :status AND m.studentId IN :studentIds " +
            "GROUP BY m.studentId")
    List<StudentMakeupSummary> summarizeByStudent(
            @Param("classId") Long classId,
            @Param("status") MakeupApprovalStatus status,
            @Param("studentIds") Collection<Long> studentIds
    );

    // 학생별 보강 집계 결과 프로젝션
    interface StudentMakeupSummary {
        Long getStudentId();

        long getMakeupCount();
    }
}
