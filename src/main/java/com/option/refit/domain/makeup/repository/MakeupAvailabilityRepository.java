package com.option.refit.domain.makeup.repository;

import org.springframework.data.jpa.repository.Modifying;
import com.option.refit.domain.makeup.entity.MakeupAvailability;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MakeupAvailabilityRepository extends JpaRepository<MakeupAvailability, Long> {

    // 보강 가능 시간 중복 등록 확인
    boolean existsByClassIdAndScheduleIdAndMakeupTime(Long classId, Long scheduleId, LocalDateTime makeupTime);

    // 정원 체크 시 동시 신청으로 정원을 넘지 않도록 행을 잠그고 조회
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM MakeupAvailability a " +
            "WHERE a.classId = :classId AND a.scheduleId = :scheduleId AND a.makeupTime = :makeupTime")
    Optional<MakeupAvailability> findForUpdate(
            @Param("classId") Long classId,
            @Param("scheduleId") Long scheduleId,
            @Param("makeupTime") LocalDateTime makeupTime
    );

    // 학생 신청용: 일정(schedule)과 무관하게 수업·시간으로 보강 가능 시간을 찾는다 (가능 시간은 수업 단위로 열려 있고
    // schedule_id는 등록 시 지정한 일정일 뿐이라, 다른 학생도 같은 시간을 고를 수 있어야 한다). 같은 시간이 여러 건이면 id가 작은 건이 기준
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM MakeupAvailability a " +
            "WHERE a.classId = :classId AND a.makeupTime = :makeupTime ORDER BY a.id ASC")
    List<MakeupAvailability> findAllForUpdateByClassAndTime(
            @Param("classId") Long classId,
            @Param("makeupTime") LocalDateTime makeupTime
    );

    // 수정 시 자기 자신을 제외하고 같은 수업·일정·시간이 이미 있는지 확인
    boolean existsByClassIdAndScheduleIdAndMakeupTimeAndIdNot(
            Long classId, Long scheduleId, LocalDateTime makeupTime, Long id);

    // 수정·삭제 시 동시 신청과 엇갈리지 않도록 행을 잠그고 조회
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM MakeupAvailability a WHERE a.id = :id")
    Optional<MakeupAvailability> findByIdForUpdate(@Param("id") Long id);

    // 보강 가능 시간 목록 조회: 수업(선택), 선생님(선택)으로 필터링, 가까운 시간 순
    @Query("SELECT a FROM MakeupAvailability a " +
            "WHERE (:classId IS NULL OR a.classId = :classId) " +
            "AND (:teacherId IS NULL OR a.teacherId = :teacherId) " +
            "AND (:scheduleId IS NULL OR a.scheduleId = :scheduleId) " +
            "ORDER BY a.makeupTime ASC, a.id ASC")
    List<MakeupAvailability> findList(
            @Param("classId") Long classId,
            @Param("teacherId") Long teacherId,
            @Param("scheduleId") Long scheduleId
    );

    // 수업 삭제 시 해당 수업의 보강 가능 시간을 모두 삭제
    @Modifying
    @Query("DELETE FROM MakeupAvailability a WHERE a.classId = :classId")
    void deleteAllByClassId(@Param("classId") Long classId);
}
