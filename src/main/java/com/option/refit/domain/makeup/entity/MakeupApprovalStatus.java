package com.option.refit.domain.makeup.entity;

import java.util.List;

// ORDINAL로 저장되므로 순서 변경 금지 (0: 대기, 1: 승인, 2: 거절, 3: 취소)
public enum MakeupApprovalStatus {
    PENDING,
    APPROVED,
    REJECTED,
    CANCELLED;

    // 정원·중복·변경 가능 여부 판정에서 자리를 차지하지 않는 상태 (거절·취소)
    public static final List<MakeupApprovalStatus> INACTIVE = List.of(REJECTED, CANCELLED);
}
