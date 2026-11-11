package com.option.refit.domain.nfc.repository;

import com.option.refit.domain.nfc.entity.NfcTag;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// 소프트 삭제된 태그는 엔티티의 @SQLRestriction으로 모든 조회에서 제외된다.
@Repository
public interface NfcTagRepository extends JpaRepository<NfcTag, Long> {

    Optional<NfcTag> findByToken(String token);

    Optional<NfcTag> findByClassId(Long classId);

    List<NfcTag> findAllByTeacherIdOrderByCreatedAtDesc(Long teacherId);

    // 연결·해제·삭제를 같은 태그에 동시에 요청해도 순차 처리되도록 행 잠금
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM NfcTag t WHERE t.id = :id")
    Optional<NfcTag> findByIdForUpdate(@Param("id") Long id);
}
