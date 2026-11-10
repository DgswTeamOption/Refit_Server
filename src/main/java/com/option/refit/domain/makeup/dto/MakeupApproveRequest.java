package com.option.refit.domain.makeup.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 보강 승인/거절: approval_status (1: 승인, 2: 거절) - makeup_id는 경로 변수 사용
@Getter
@NoArgsConstructor
public class MakeupApproveRequest {

    @JsonProperty("approval_status")
    @NotNull(message = "승인 상태는 필수입니다.")
    private Integer approvalStatus;
}
