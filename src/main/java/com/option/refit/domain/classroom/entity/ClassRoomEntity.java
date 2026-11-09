package com.option.refit.domain.classroom.entity;

import com.option.refit.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter

@NoArgsConstructor(access = AccessLevel.PROTECTED)

// 데이터베이스에서 사용할 테이블 이름을 classroom으로 지정
@Table(name = "classroom")
public class ClassRoomEntity {

    //PK 역할을 하는 필드다.
    @Id

    // 데이터베이스가 기본 키 값을 자동으로 생성하도록 설정
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 수업 이름을 저장
    @Column(nullable = false, length = 100)
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

    // 수업 이름과 교사 정보를 전달받아 객체를 생성하는 생성자이며 동시에 수업 참여 코드를 생성
    private ClassRoomEntity(String name, User teacher) {
        this.name = name;
        this.teacher = teacher;
        this.classCode = generateClassCode();
    }

    // 외부에서 수업 엔티티를 생성할 때 사용하는 정적 팩토리 메서드
    // private 생성자에 직접 접근하지 않고 이 메서드를 통해 객체를 생성
    public static ClassRoomEntity create(String name, User teacher) {
        return new ClassRoomEntity(name, teacher);
    }

    // UUID를 활용하여 8자리의 무작위 수업 참여 코드를 생성
    private String generateClassCode() {
        return UUID.randomUUID()
                .toString()             // UUID를 문자열로 변환한다.
                .replace("-", "")       // UUID에 포함된 하이픈(-)을 제거한다.
                .substring(0, 8)        // 앞에서부터 8자리만 추출한다.
                .toUpperCase();         // 영문자를 대문자로 변환한다.
    }
}