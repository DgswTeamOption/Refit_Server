package com.option.refit.domain.classroom.repository;

import com.option.refit.domain.classroom.entity.ClassRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClassRoomRepository
        extends JpaRepository<ClassRoomEntity, Long> {

        Optional<ClassRoomEntity> findByClassCode(String classCode);

        List<ClassRoomEntity> findAllByTeacher_IdOrderByCreatedAtDesc(
                Long teacherId
        );
}

