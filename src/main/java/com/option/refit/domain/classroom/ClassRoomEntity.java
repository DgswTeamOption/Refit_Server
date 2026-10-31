package com.option.refit.domain.classroom;

import com.option.refit.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

public class ClassRoomEntity {

    @Entity
    @Table(name = "classrooms")
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public class ClassroomEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false, length = 100)
        private String name;

        @Column(nullable = false, unique = true, length = 8)
        private String classCode;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "teacher_id", nullable = false)
        private User teacher;

        @CreationTimestamp
        @Column(nullable = false, updatable = false)
        private LocalDateTime createdAt;

        private ClassroomEntity(String name, User teacher) {
            this.name = name;
            this.teacher = teacher;
            this.classCode = generateClassCode();
        }

        public static ClassroomEntity create(String name, User teacher) {
            return new ClassRoomEntity(name, teacher);
        }

        private String generateClassCode() {
            return UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, 8)
                    .toUpperCase();
        }
    }
}
