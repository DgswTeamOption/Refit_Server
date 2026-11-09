package com.option.refit.domain.classroom.repository;

import com.option.refit.domain.classroom.entity.ClassRoomMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// ClassRoomMemberEntity를 데이터베이스에서 관리하는 Repository

public interface ClassRoomMemberRepository
        extends JpaRepository<ClassRoomMemberEntity, Long> {
        // JpaRepository를 상속받아 기본적인 CRUD 기능 사용 가능

    // 특정 수업에 특정 사용자가 참여한 기록을 조회한다.
    // Optional을 사용하여 조회 결과가 없을 수도 있는 상황을 처리
    Optional<ClassRoomMemberEntity> findByClassroom_IdAndUser_Id(
            Long classroomId,
            Long userId
    );

    // 특정 사용자가 현재 참여 중인 수업 목록을 조회
    // LeftAt이 null인 경우 수업에서 나가지 않은 상태
    // JoinedAt을 기준으로 최근에 참여한 수업부터 정렬
    List<ClassRoomMemberEntity>
    findAllByUser_IdAndLeftAtIsNullOrderByJoinedAtDesc(Long userId);

    // 특정 사용자가 특정 수업에 현재 참여 중인지 확인
    // 참여 기록이 존재하고 LeftAt이 null이면 true를 반환
    boolean existsByClassroom_IdAndUser_IdAndLeftAtIsNull(
            Long classroomId,
            Long userId
    );

    // 특정 수업에 현재 참여 중인 인원수를 조회하며 LeftAt이 null인 참여 기록만 계산
    long countByClassroom_IdAndLeftAtIsNull(Long classroomId);
}