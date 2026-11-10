package com.option.refit.domain.classroom.repository;

import com.option.refit.domain.classroom.entity.ClassRoomMemberEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

// ClassRoomMemberEntity를 데이터베이스에서 관리하는 Repository
public interface ClassRoomMemberRepository
        extends JpaRepository<ClassRoomMemberEntity, Long> {

    // 특정 수업에 특정 사용자가 참여한 기록을 조회한다.
    Optional<ClassRoomMemberEntity> findByClassroom_IdAndUser_Id(
            Long classroomId,
            Long userId
    );

    // 특정 사용자의 참여 중인 수업 목록과 교사 정보를 함께 조회한다.
    @EntityGraph(attributePaths = {"classroom", "classroom.teacher"})
    List<ClassRoomMemberEntity>
    findAllByUser_IdAndLeftAtIsNullOrderByJoinedAtDesc(Long userId);

    // 특정 사용자가 특정 수업에 현재 참여 중인지 확인
    boolean existsByClassroom_IdAndUser_IdAndLeftAtIsNull(
            Long classroomId,
            Long userId
    );

    // 특정 수업에 현재 참여 중인 인원수를 조회
    long countByClassroom_IdAndLeftAtIsNull(Long classroomId);

    // 참여 중인 학생과 User를 한 번의 쿼리로 조회하여 N+1 방지
    @Query("""
            select member
            from ClassRoomMemberEntity member
            join fetch member.user student
            where member.classroom.id = :classroomId
              and member.leftAt is null
            order by member.joinedAt asc
            """)
    List<ClassRoomMemberEntity> findActiveMembersWithUser(@Param("classroomId") Long classroomId);
}