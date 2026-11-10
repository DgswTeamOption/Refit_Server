package com.option.refit.domain.classroom.entity;

import com.option.refit.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)

// 수업 삭제 시 실제 데이터를 삭제하지 않고 deleted_at에 삭제 시각을 기록하는 소프트 삭제 설정
@SQLDelete(sql = "UPDATE classroom SET deleted_at = NOW() WHERE id = ?")

// 조회 시 deleted_at이 NULL인, 삭제되지 않은 수업만 조회하도록 설정
@SQLRestriction("deleted_at IS NULL")

// 데이터베이스에서 사용할 테이블 이름을 classroom으로 지정
@Table(name = "classroom")
public class ClassRoomEntity {

    //PK 역할을 하는 필드다.
    @Id

    // 데이터베이스가 기본 키 값을 자동으로 생성하도록 설정
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 수업 이름을 저장
    @Column(nullable = false, length = 50)
    private String name;

    // 수업 참여 시 사용하는 고유 코드
    @Column(nullable = false, unique = true, length = 8)
    private String classCode;

    // 수업을 생성한 교사 정보를 저장
    @ManyToOne(fetch = FetchType.LAZY) //LAZY는 실제로 교사 정보가 필요할 때 조회하도록 하는 지연 로딩 설정
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher;

    // 수업이 처음 생성된 시간을 자동으로 기록하여 저장
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 수업 정보와 외부에서 생성한 참여 코드로 객체 생성
    private ClassRoomEntity(String name, User teacher, String classCode) {
        this.name = name;
        this.teacher = teacher;
        this.classCode = classCode;
    }

    // 소프트 삭제가 수행된 날짜와 시간
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // 수업 엔티티 생성
    public static ClassRoomEntity create(String name, User teacher, String classCode) {
        return new ClassRoomEntity(name, teacher, classCode);
    }
}