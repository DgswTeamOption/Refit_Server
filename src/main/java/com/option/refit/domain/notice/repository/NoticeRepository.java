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

    // 수업 삭제 시 공지를 소프트 삭제 (@SQLDelete 적용)
    void deleteAllByClassId(Long classId);
}
