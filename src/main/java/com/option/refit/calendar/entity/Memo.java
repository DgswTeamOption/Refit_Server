package com.option.refit.calendar.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name =  "memo")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE memo SET deleted_at = NOW() WHERE id = ?")
public class Memo extends Base{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "schedule_id", nullable = false)
    private Long scheduleId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "memo_name", nullable = false, length = 255)
    private String memoName;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Builder
    public Memo(Long scheduleId, Long userId, String memoName, String content) {
        this.scheduleId = scheduleId;
        this.userId = userId;
        this.memoName = memoName;
        this.content = content;
    }

    public void updateMemo(String memoName, String content) {
        this.memoName = memoName;
        this.content = content;
    }
}
