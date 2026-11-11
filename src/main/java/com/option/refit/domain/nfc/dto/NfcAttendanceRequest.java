package com.option.refit.domain.nfc.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 학생의 NFC 출석: 태그에서 읽은 토큰과 출석할 수업 회차. 학생 ID·역할은 인증 정보에서만 가져온다.
@Getter
@NoArgsConstructor
public class NfcAttendanceRequest {

    @NotBlank(message = "NFC 토큰은 필수입니다.")
    @Size(max = 64, message = "NFC 토큰 형식이 올바르지 않습니다.")
    private String token;

    @JsonProperty("schedule_id")
    @NotNull(message = "회차 ID는 필수입니다.")
    private Long scheduleId;
}
