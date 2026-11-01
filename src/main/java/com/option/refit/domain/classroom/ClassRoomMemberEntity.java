package com.option.refit.domain.classroom;

import com.option.refit.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class ClassRoomMemberEntity {

    // 수업과 학생의 참여 관계를 저장

    @Entity
    @Table(
            name = "classroom_members",
            uniqueConstraints = {
                    @UniqueConstraint(
                            columnNames = {"classroom_id", "user_id"}
                    )
            }
    )
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public class ClassroomMemberEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "classroom_id", nullable = false)
        private ClassRoomEntity classroom;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id", nullable = false)
        private User user;

        @Column(nullable = false)
        private LocalDateTime joinedAt;

        private LocalDateTime leftAt;

        private ClassroomMemberEntity(
                ClassRoomEntity classroom,
                User user
        ) {
            this.classroom = classroom;
            this.user = user;
            this.joinedAt = LocalDateTime.now();
        }

        public static ClassroomMemberEntity create(
                ClassRoomEntity classroom,
                User user
        ) {
            return new ClassRoomMemberEntity(classroom, user);
        }

        public boolean isActive() {
            return leftAt == null;
        }

        public void leave() {
            this.leftAt = LocalDateTime.now();
        }

        public void rejoin() {
            this.joinedAt = LocalDateTime.now();
            this.leftAt = null;
        }
    }

}
