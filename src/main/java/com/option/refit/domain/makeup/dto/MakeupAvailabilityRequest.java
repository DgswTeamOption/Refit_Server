package com.option.refit.domain.makeup.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

// 보강 가능 시간 등록: class_id, schedule_id, makeup_time[] (+ 선택: capacity, 생략 시 1명)
@Getter
@NoArgsConstructor
public class MakeupAvailabilityRequest {

    @JsonProperty("class_id")
    @NotNull(message = "수업은 필수입니다.")
    private Long classId;

    @JsonProperty("schedule_id")
    @NotNull(message = "수업 일정은 필수입니다.")
    private Long scheduleId;

    // 형식: 2026-10-25T14:00:00 (여러 시간대를 한 번에 등록)
    @JsonProperty("makeup_time")
    @ArraySchema(
            arraySchema = @Schema(description = "보강 가능 시간 목록 (여러 개 한 번에 등록)"),
            schema = @Schema(type = "string", format = "date-time", example = "2026-10-25T14:00:00"))
    @NotEmpty(message = "보강 시간은 최소 1개 이상 필요합니다.")
    private List<@NotNull(message = "보강 시간은 필수입니다.") LocalDateTime> makeupTimes;

    @Min(value = 1, message = "정원은 1명 이상이어야 합니다.")
    private Integer capacity;

    @JsonIgnore
    @Schema(hidden = true)
    public int getCapacityOrDefault() {
        return capacity == null ? 1 : capacity;
    }
}
