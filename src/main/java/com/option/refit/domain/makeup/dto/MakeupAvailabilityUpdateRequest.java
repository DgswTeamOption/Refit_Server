package com.option.refit.domain.makeup.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// 보강 가능 시간 수정: 바꿀 항목만 보낸다 (둘 다 생략하면 변경 없음)
@Getter
@NoArgsConstructor
public class MakeupAvailabilityUpdateRequest {

    // 형식: 2026-10-25T14:00:00
    @JsonProperty("makeup_time")
    private LocalDateTime makeupTime;

    @Min(value = 1, message = "정원은 1명 이상이어야 합니다.")
    private Integer capacity;
}
