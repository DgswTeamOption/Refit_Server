package com.option.refit.domain.nfc.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

// NFC 태그 수정: 보낸 필드만 변경한다 (이름, 활성 여부)
@Getter
@NoArgsConstructor
public class NfcTagUpdateRequest {

    @Size(min = 1, max = 50, message = "태그 이름은 1자 이상 50자 이내로 입력해주세요.")
    @Pattern(regexp = ".*\\S.*", message = "태그 이름은 공백일 수 없습니다.")
    private String name;

    @JsonProperty("is_active")
    private Boolean active;
}
