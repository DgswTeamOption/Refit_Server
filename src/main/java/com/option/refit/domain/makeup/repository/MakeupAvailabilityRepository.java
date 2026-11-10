package com.option.refit.domain.makeup.repository;

import com.option.refit.domain.makeup.entity.MakeupAvailability;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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
}
