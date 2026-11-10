package com.option.refit.domain.attendance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

// NFC 태그 등록·인증·출석 요청: 태그에서 읽은 고유값(tag_uid). 수업은 경로의 classId 사용
@Getter
@NoArgsConstructor
public class NfcTagRequest {

    @JsonProperty("tag_uid")
    @NotBlank(message = "NFC 태그 값은 필수입니다.")
    @Size(max = 100, message = "NFC 태그 값은 100자 이내로 입력해주세요.")
    private String tagUid;
}
