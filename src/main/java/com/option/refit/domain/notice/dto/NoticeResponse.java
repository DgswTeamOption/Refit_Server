package com.option.refit.domain.notice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.option.refit.domain.notice.entity.NoticeEntity;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NoticeResponse {

    private final Long noticeId;
    private final Long classId;
    private final Long writerId;
    private final String title;
    private final String content;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime updatedAt;

    public NoticeResponse(NoticeEntity entity) {
        this.noticeId = entity.getId();
        this.classId = entity.getClassId();
        this.writerId = entity.getWriterId();
        this.title = entity.getTitle();
        this.content = entity.getContent();
        this.createdAt = entity.getCreatedAt();
        this.updatedAt = entity.getUpdatedAt();
    }
}
