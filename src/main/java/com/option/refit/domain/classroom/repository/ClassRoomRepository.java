package com.option.refit.domain.classroom.repository;

import com.option.refit.domain.classroom.entity.ClassRoomEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

// ClassRoomEntity를 데이터베이스에서 관리하는 Repository

public interface ClassRoomRepository
        extends JpaRepository<ClassRoomEntity, Long> {

        // 수업 참여 코드를 이용하여 수업 정보를 조회
        Optional<ClassRoomEntity> findByClassCode(String classCode);

        // 수업 코드 중복 여부 확인
        boolean existsByClassCode(String classCode);

        // 특정 교사가 만든 수업 ID만 조회 (집계용, 엔티티 전체를 읽지 않는다)
        @Query("SELECT c.id FROM ClassRoomEntity c WHERE c.teacher.id = :teacherId")
        List<Long> findIdsByTeacherId(@Param("teacherId") Long teacherId);

        // 특정 교사의 수업 목록 조회 시 교사 정보도 함께 조회
        @EntityGraph(attributePaths = {"teacher", "bannerImage"})
        List<ClassRoomEntity> findAllByTeacher_IdOrderByCreatedAtDesc(Long teacherId);
}