package com.option.refit.domain.attendance.repository;

import com.option.refit.domain.attendance.entity.NfcTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NfcTagRepository extends JpaRepository<NfcTag, Long> {

    Optional<NfcTag> findByTagUid(String tagUid);

    boolean existsByTagUid(String tagUid);
}
