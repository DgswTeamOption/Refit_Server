package com.option.refit.domain.classroom.entity;

import com.option.refit.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "classroom_members",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"classroom_id", "user_id"}
                )
        }
)
public class ClassRoomMemberEntity {

    // 고유 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 참여 중인 수업
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classroom_id", nullable = false)
    private ClassRoomEntity classroom;

    // 참여한 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 참여 날짜
    @Column(nullable = false)
    private LocalDateTime joinedAt;

    // 나간 날짜
    private LocalDateTime leftAt;

    // 생성자
    private ClassRoomMemberEntity(
            ClassRoomEntity classroom,
            User user
    ) {
        this.classroom = classroom;
        this.user = user;
        this.joinedAt = LocalDateTime.now();
    }

    // 객체 생성
    public static ClassRoomMemberEntity create(
            ClassRoomEntity classroom,
            User user
    ) {
        return new ClassRoomMemberEntity(classroom, user);
    }

    // 현재 참여 중인지 확인
    public boolean isActive() {
        return leftAt == null;
    }

    // 수업 나가기
    public void leave() {
        this.leftAt = LocalDateTime.now();
    }

    // 수업 재참여
    public void rejoin() {
        this.joinedAt = LocalDateTime.now();
        this.leftAt = null;
    }
}
