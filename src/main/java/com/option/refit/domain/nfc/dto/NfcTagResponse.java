package com.option.refit.domain.nfc.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.option.refit.domain.nfc.entity.NfcTag;
import lombok.Getter;

import java.time.LocalDateTime;

// 소유 선생님에게만 내려가는 응답. token과 url은 스티커에 기록할 값이다.
@Getter
public class NfcTagResponse {

    private final Long id;

    private final String name;

    @JsonProperty("class_id")
    private final Long classId;

    @JsonProperty("is_active")
    private final boolean active;

    private final String token;

    // 스티커에 NDEF URI 레코드로 기록할 출석 주소 (기본 주소는 nfc.attendance-url 설정)
    private final String url;

    @JsonProperty("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime createdAt;

    public NfcTagResponse(NfcTag tag, String url) {
        this.id = tag.getId();
        this.name = tag.getName();
        this.classId = tag.getClassId();
        this.active = tag.isActive();
        this.token = tag.getToken();
        this.url = url;
        this.createdAt = tag.getCreatedAt();
    }
}
