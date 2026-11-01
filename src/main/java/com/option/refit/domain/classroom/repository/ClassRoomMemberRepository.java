package com.option.refit.domain.classroom.repository;

import com.option.refit.domain.classroom.entity.ClassRoomMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public class ClassRoomMemberRepository {

    public interface ClassroomMemberRepository
            extends JpaRepository<ClassRoomMemberEntity, Long> {

        Optional<ClassRoomMemberEntity>
        findByClassroom_IdAndUser_Id(Long classroomId, Long userId);

        List<ClassRoomMemberEntity>
        findAllByUser_IdAndLeftAtIsNullOrderByJoinedAtDesc(Long userId);

        boolean existsByClassroom_IdAndUser_IdAndLeftAtIsNull(
                Long classroomId,
                Long userId
        );
    }

}
