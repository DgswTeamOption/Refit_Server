package com.option.refit.domain.absence.repository;

import com.option.refit.domain.absence.entity.Absence;
import com.option.refit.domain.absence.entity.AbsenceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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
}
