package com.option.refit.domain.nfc.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

// NFC 태그 등록: 선생님이 구분하기 위한 이름만 받는다. 토큰은 서버가 생성한다.
@Getter
@NoArgsConstructor
public class NfcTagCreateRequest {

    @NotBlank(message = "태그 이름은 필수입니다.")
    @Size(max = 50, message = "태그 이름은 50자 이내로 입력해주세요.")
    private String name;
}
