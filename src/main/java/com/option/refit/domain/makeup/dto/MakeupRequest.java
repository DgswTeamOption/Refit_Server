package com.option.refit.domain.makeup.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// 보강 신청: class_id, makeup_time (+ 선택: schedule_id)
// schedule_id가 있으면 선생님이 등록한 가능 시간 중 선택, 없으면 학생이 직접 시간을 제안

@Getter
@NoArgsConstructor
public class MakeupRequest {

    @JsonProperty("class_id")
    @NotNull(message = "수업은 필수입니다.")
    private Long classId;

    @JsonProperty("schedule_id")
    private Long scheduleId;

    @JsonProperty("makeup_time")
    @NotNull(message = "보강 시간은 필수입니다.")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime makeupTime;
}
