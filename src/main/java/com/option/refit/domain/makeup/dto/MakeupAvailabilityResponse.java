package com.option.refit.domain.makeup.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.option.refit.domain.makeup.entity.MakeupAvailability;
import lombok.Getter;

import java.time.LocalDateTime;

// 보강 가능 시간 응답.
// 신청 인원은 대기+승인만 집계한다 (거절·취소는 자리를 차지하지 않으므로 제외).
// can_change_time / can_delete 는 "신청 현황상" 가능한지만 나타내며, 담당 선생님 여부는 별개다.
@Getter
public class MakeupAvailabilityResponse {

    // 신청이 있어 시간 변경·삭제가 막힌 경우의 안내 사유
    public static final String LOCK_REASON_HAS_APPLICATIONS =
            "대기 또는 승인된 보강 신청이 있어 시간 변경 및 삭제를 할 수 없습니다";

    private final Long id;

    @JsonProperty("class_id")
    private final Long classId;

    @JsonProperty("teacher_id")
    private final Long teacherId;

    @JsonProperty("schedule_id")
    private final Long scheduleId;

    @JsonProperty("makeup_time")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime makeupTime;

    private final int capacity;

    // 대기 상태 신청 수
    @JsonProperty("pending_count")
    private final long pendingCount;

    // 승인 상태 신청 수
    @JsonProperty("approved_count")
    private final long approvedCount;

    // 자리를 차지하는 신청 수 (대기 + 승인)
    @JsonProperty("applied_count")
    private final long appliedCount;

    // 남은 정원 (정원 - 신청 수, 0 미만이면 0)
    @JsonProperty("remaining_capacity")
    private final long remainingCapacity;

    // 마감 여부 (남은 정원이 없음)
    @JsonProperty("is_full")
    private final boolean full;

    // 정원을 줄일 수 있는 최솟값 (현재 신청 수와 1 중 큰 값)
    @JsonProperty("min_capacity")
    private final long minCapacity;

    // 시간 변경 가능 여부 (신청이 없을 때만 true)
    @JsonProperty("can_change_time")
    private final boolean canChangeTime;

    // 삭제 가능 여부 (신청이 없을 때만 true)
    @JsonProperty("can_delete")
    private final boolean canDelete;

    // 변경·삭제가 불가능한 이유, 가능하면 null
    @JsonProperty("lock_reason")
    private final String lockReason;

    public MakeupAvailabilityResponse(MakeupAvailability availability) {
        this(availability, 0, 0);
    }

    public MakeupAvailabilityResponse(MakeupAvailability availability, long pendingCount, long approvedCount) {
        this.id = availability.getId();
        this.classId = availability.getClassId();
        this.teacherId = availability.getTeacherId();
        this.scheduleId = availability.getScheduleId();
        this.makeupTime = availability.getMakeupTime();
        this.capacity = availability.getCapacity();
        this.pendingCount = pendingCount;
        this.approvedCount = approvedCount;
        this.appliedCount = pendingCount + approvedCount;
        this.remainingCapacity = Math.max(0, availability.getCapacity() - appliedCount);
        this.full = remainingCapacity == 0;
        this.minCapacity = Math.max(1, appliedCount);
        boolean editable = appliedCount == 0;
        this.canChangeTime = editable;
        this.canDelete = editable;
        this.lockReason = editable ? null : LOCK_REASON_HAS_APPLICATIONS;
    }
}
