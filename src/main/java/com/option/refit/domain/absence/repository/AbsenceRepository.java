package com.option.refit.domain.absence.repository;

import com.option.refit.domain.absence.entity.Absence;
import com.option.refit.domain.absence.entity.AbsenceStatus;
import com.option.refit.domain.makeup.entity.MakeupApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

// 삭제된 건은 Absence의 @SQLRestriction으로 항상 제외됨
@Repository
public interface AbsenceRepository extends JpaRepository<Absence, Long> {

    // 학생별 결석 내역 (전체)
    List<Absence> findByStudentIdOrderByAbsenceDateDesc(Long studentId);

    // 학생별 결석 내역 (취소 건 제외)
    List<Absence> findByStudentIdAndStatusNotOrderByAbsenceDateDesc(Long studentId, AbsenceStatus status);

    // 수업별 결석 내역 (전체)
    List<Absence> findByClassIdOrderByAbsenceDateDesc(Long classId);

    // 수업별 결석 내역 (취소 건 제외)
    List<Absence> findByClassIdAndStatusNotOrderByAbsenceDateDesc(Long classId, AbsenceStatus status);

    // 수업별 + 상태별 결석 내역
    List<Absence> findByClassIdAndStatusOrderByAbsenceDateDesc(Long classId, AbsenceStatus status);

    // 같은 학생이 같은 수업/날짜에 취소되지 않은 신청을 이미 했는지 (중복 신청 방지)
    boolean existsByStudentIdAndClassIdAndAbsenceDateAndStatusNot(
            Long studentId, Long classId, LocalDate absenceDate, AbsenceStatus status);

    // 수업별 + 학생별 결석 내역 (취소 건 포함)
    List<Absence> findByClassIdAndStudentIdOrderByAbsenceDateDesc(Long classId, Long studentId);

    // 수업별 결석 내역 선택 필터 (status, 날짜 범위: null이면 조건 무시)
    @Query("""
            select a from Absence a
            where a.classId = :classId
              and (:status is null or a.status = :status)
              and (:from is null or a.absenceDate >= :from)
              and (:to is null or a.absenceDate <= :to)
            order by a.absenceDate desc, a.id desc
            """)
    List<Absence> search(@Param("classId") Long classId,
                         @Param("status") AbsenceStatus status,
                         @Param("from") LocalDate from,
                         @Param("to") LocalDate to);

    Optional<Absence> findByIdAndClassId(Long id, Long classId);

    // 수업 내 학생별 결석 횟수와 최근 결석일 집계 (상태 필터, 학생 목록 한 번에 조회)
    @Query("SELECT a.studentId AS studentId, COUNT(a) AS absenceCount, MAX(a.absenceDate) AS lastAbsenceDate " +
            "FROM Absence a " +
            "WHERE a.classId = :classId AND a.status = :status AND a.studentId IN :studentIds " +
            "GROUP BY a.studentId")
    List<StudentAbsenceSummary> summarizeByStudent(
            @Param("classId") Long classId,
            @Param("status") AbsenceStatus status,
            @Param("studentIds") Collection<Long> studentIds
    );

    // 학생별 결석 집계 결과 프로젝션
    interface StudentAbsenceSummary {
        Long getStudentId();

        long getAbsenceCount();

        LocalDate getLastAbsenceDate();
    }

    // 여러 수업의 기간 내 특정 상태 결석 건수 (챗봇 월말 현황용)
    long countByClassIdInAndStatusAndAbsenceDateBetween(
            Collection<Long> classIds, AbsenceStatus status, LocalDate from, LocalDate to);

    // 보강이 아직 잡히지 않은 학생·수업별 결석 집계 (챗봇 미제안 학생용)
    // 해당 수업에 현재 참여 중인 학생만, 거절·취소를 제외한 보강 신청이 하나도 없는 경우만 남긴다
    @Query("SELECT a.studentId AS studentId, a.classId AS classId, COUNT(a) AS absenceCount, " +
            "MAX(a.absenceDate) AS lastAbsenceDate " +
            "FROM Absence a " +
            "WHERE a.classId IN :classIds AND a.status = :status " +
            "AND EXISTS (SELECT 1 FROM ClassRoomMemberEntity cm " +
            "WHERE cm.classroom.id = a.classId AND cm.user.id = a.studentId AND cm.leftAt IS NULL) " +
            "AND NOT EXISTS (SELECT 1 FROM Makeup m " +
            "WHERE m.studentId = a.studentId AND m.classId = a.classId AND m.approvalStatus NOT IN :inactiveMakeupStatuses) " +
            "GROUP BY a.studentId, a.classId " +
            "ORDER BY MAX(a.absenceDate) DESC")
    List<UnproposedAbsenceSummary> summarizeUnproposed(
            @Param("classIds") Collection<Long> classIds,
            @Param("status") AbsenceStatus status,
            @Param("inactiveMakeupStatuses") Collection<MakeupApprovalStatus> inactiveMakeupStatuses
    );

    // 보강 미제안 결석 집계 결과 프로젝션
    interface UnproposedAbsenceSummary {
        Long getStudentId();

        Long getClassId();

        long getAbsenceCount();

        LocalDate getLastAbsenceDate();
    }

    // 수업 삭제 시 결석 신청을 소프트 삭제 (@SQLDelete 적용)
    void deleteAllByClassId(Long classId);
}
