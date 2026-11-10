package com.option.refit.domain.makeup.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.option.refit.domain.makeup.entity.Makeup;
import lombok.Getter;

@Getter
public class MakeupApproveResponse {

    private final Long id;

    @JsonProperty("approval_status")
    private final int approvalStatus;

    public MakeupApproveResponse(Makeup makeup) {
        this.id = makeup.getId();
        this.approvalStatus = makeup.getApprovalStatus().ordinal();
    }
}
