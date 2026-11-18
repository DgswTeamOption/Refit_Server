package com.option.refit.domain.nfc.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.option.refit.domain.nfc.entity.NfcTag;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;

// 소유 선생님에게만 내려가는 응답. token과 url은 스티커에 기록할 값이라 출석 위조에 쓰일 수 있으므로
// 태그 등록 직후 응답에만 담고, 조회·수정·연결 응답에는 포함하지 않는다(null이면 필드 자체를 생략).
@Getter
public class NfcTagResponse {

    private final Long id;

    private final String name;

    @JsonProperty("class_id")
    private final Long classId;

    @JsonProperty("is_active")
    private final boolean active;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "태그 식별 토큰. 태그 등록 응답에서만 내려가며 이후 조회에서는 제공되지 않음")
    private final String token;

    // 스티커에 NDEF URI 레코드로 기록할 출석 주소 (기본 주소는 nfc.attendance-url 설정)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "스티커에 기록할 출석 주소. 태그 등록 응답에서만 내려가며 이후 조회에서는 제공되지 않음")
    private final String url;

    @JsonProperty("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime createdAt;

    private NfcTagResponse(NfcTag tag, String token, String url) {
        this.id = tag.getId();
        this.name = tag.getName();
        this.classId = tag.getClassId();
        this.active = tag.isActive();
        this.token = token;
        this.url = url;
        this.createdAt = tag.getCreatedAt();
    }

    // 토큰·URL 없이 (조회·수정·연결 응답)
    public static NfcTagResponse of(NfcTag tag) {
        return new NfcTagResponse(tag, null, null);
    }

    // 토큰·URL 포함 (태그 등록 직후 1회)
    public static NfcTagResponse withCredentials(NfcTag tag, String url) {
        return new NfcTagResponse(tag, tag.getToken(), url);
    }
}
