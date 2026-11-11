package com.option.refit.domain.nfc.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 수업에 연결할 태그. 수업은 경로의 classId를 사용한다.
@Getter
@NoArgsConstructor
public class NfcTagLinkRequest {

    @JsonProperty("tag_id")
    @NotNull(message = "태그 ID는 필수입니다.")
    private Long tagId;
}
