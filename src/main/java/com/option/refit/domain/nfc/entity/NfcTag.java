package com.option.refit.domain.nfc.entity;

import com.option.refit.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

// 선생님이 소유한 NFC 태그. 수업에는 최대 1개만, 태그는 수업 1개에만 연결된다(class_id 유일).
// token은 서버가 만든 식별 값일 뿐이며 로그인 인증을 대체하지 않는다. 삭제는 deleted_at 소프트 삭제.
@Entity
@Getter
@Table(
        name = "nfc_tags",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_nfc_tags_token", columnNames = "token"),
                @UniqueConstraint(name = "uk_nfc_tags_class", columnNames = "class_id")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE nfc_tags SET deleted_at = NOW() WHERE id = ?")
public class NfcTag extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 태그를 소유한 선생님
    @Column(name = "teacher_id", nullable = false)
    private Long teacherId;

    // 연결된 수업. 연결되지 않았으면 null
    @Column(name = "class_id")
    private Long classId;

    @Column(name = "token", nullable = false, length = 64, updatable = false)
    private String token;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder
    public NfcTag(Long teacherId, String token, String name) {
        this.teacherId = teacherId;
        this.token = token;
        this.name = name;
        this.active = true;
    }

    public boolean isOwnedBy(Long userId) {
        return teacherId.equals(userId);
    }

    public boolean isLinked() {
        return classId != null;
    }

    public void update(String name, Boolean active) {
        if (name != null) {
            this.name = name;
        }
        if (active != null) {
            this.active = active;
        }
    }

    public void linkTo(Long classId) {
        this.classId = classId;
    }

    public void unlink() {
        this.classId = null;
    }
}
