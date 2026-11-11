package com.option.refit.domain.classroom.repository;

import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import com.option.refit.domain.classroom.entity.ClassRoomMemberEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

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

    // 특정 수업에 현재 참여 중인 수강생 명단 (학생 정보 함께 조회)
    @EntityGraph(attributePaths = "user")
    List<ClassRoomMemberEntity> findAllByClassroom_IdAndLeftAtIsNull(Long classroomId);

    // 특정 수업에 현재 참여 중인 인원수를 조회
    long countByClassroom_IdAndLeftAtIsNull(Long classroomId);

    // 특정 수업에 현재 참여 중인 학생 목록을 이름순으로 조회 (탈퇴한 사용자 제외)
    @EntityGraph(attributePaths = "user")
    List<ClassRoomMemberEntity>
    findAllByClassroom_IdAndLeftAtIsNullAndUser_DeletedAtIsNullOrderByUser_NameAsc(Long classroomId);

    // 수업 삭제 시 해당 수업의 가입 기록(탈퇴 이력 포함)을 모두 삭제
    @Modifying
    @Query("DELETE FROM ClassRoomMemberEntity m WHERE m.classroom.id = :classroomId")
    void deleteAllByClassroomId(@Param("classroomId") Long classroomId);
}
