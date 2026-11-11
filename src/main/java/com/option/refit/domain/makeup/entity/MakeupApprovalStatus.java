package com.option.refit.domain.makeup.entity;

// ORDINAL로 저장되므로 순서 변경 금지 (0: 대기, 1: 승인, 2: 거절, 3: 취소)
public enum MakeupApprovalStatus {
    PENDING,
    APPROVED,
    REJECTED,
    CANCELLED
}
