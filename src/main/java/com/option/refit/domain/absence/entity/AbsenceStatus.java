package com.option.refit.domain.absence.entity;

// ORDINAL로 저장되므로 순서 변경 금지 (0: 대기, 1: 승인, 2: 반려, 3: 취소)
public enum AbsenceStatus {
    PENDING,
    APPROVED,
    REJECTED,
    CANCELED
}
