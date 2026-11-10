package com.option.refit.domain.attendance.entity;

import com.option.refit.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 선생님이 수업에 등록한 NFC 태그. 태그 값(tag_uid)은 전체에서 유일하다.
@Entity
@Getter
@Table(
        name = "nfc_tags",
        uniqueConstraints = @UniqueConstraint(columnNames = "tag_uid")
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NfcTag extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "class_id", nullable = false)
    private Long classId;

    @Column(name = "tag_uid", nullable = false, length = 100)
    private String tagUid;

    // 태그를 등록한 선생님
    @Column(name = "registered_by", nullable = false)
    private Long registeredBy;

    @Builder
    public NfcTag(Long classId, String tagUid, Long registeredBy) {
        this.classId = classId;
        this.tagUid = tagUid;
        this.registeredBy = registeredBy;
    }

    public boolean belongsTo(Long classId) {
        return this.classId.equals(classId);
    }
}
