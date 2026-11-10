package com.option.refit.domain.notice.repository;

import com.option.refit.domain.notice.entity.NoticeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoticeRepository extends JpaRepository<NoticeEntity, Long> {

    List<NoticeEntity> findByClassIdOrderByCreatedAtDesc(Long classId);

    Optional<NoticeEntity> findByIdAndClassId(Long id, Long classId);
}
