package com.option.refit.domain.classroom.repository;

import com.option.refit.domain.classroom.entity.ClassRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// ClassRoomEntity를 데이터베이스에서 관리하는 Repository

public interface ClassRoomRepository
        extends JpaRepository<ClassRoomEntity, Long> {
        // JpaRepository를 상속받아 기본적인 CRUD 기능 사용 가능

        // 수업 참여 코드를 이용하여 수업 정보를 조회
        // 해당 코드와 일치하는 수업이 없을 수도 있으므로 Optional로 반환
        Optional<ClassRoomEntity> findByClassCode(String classCode);

        // 수업 코드 중복 여부 확인
        boolean existsByClassCode(String classCode);

        // 특정 교사가 생성한 수업 목록을 조회
        // Teacher 엔티티의 ID가 teacherId와 일치하는 수업만 가져옴
        // CreatedAt을 기준으로 최근에 생성된 수업부터 정렬
        List<ClassRoomEntity> findAllByTeacher_IdOrderByCreatedAtDesc(
                Long teacherId
        );
}