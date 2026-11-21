package com.option.refit.domain.schedule.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class ScheduleRuleRequest {

    // 반복 일정 등록: student_id, day_of_week, start_time, end_time
    @Getter
    @NoArgsConstructor
    public static class ScheduleRuleCreateRequest {
        @JsonProperty("student_id")
        @NotNull(message = "학생은 필수입니다.")
        private Long studentId;

        @JsonProperty("day_of_week")
        @NotNull(message = "요일은 필수입니다.")
        private DayOfWeek dayOfWeek;

        @JsonProperty("start_time")
        @NotNull(message = "시작 시각은 필수입니다.")
        @JsonFormat(pattern = "HH:mm")
        private LocalTime startTime;

        @JsonProperty("end_time")
        @NotNull(message = "종료 시각은 필수입니다.")
        @JsonFormat(pattern = "HH:mm")
        private LocalTime endTime;
    }

    // 반복 일정 수정: day_of_week, start_time, end_time
    @Getter
    @NoArgsConstructor
    public static class ScheduleRuleUpdateRequest {
        @JsonProperty("day_of_week")
        @NotNull(message = "요일은 필수입니다.")
        private DayOfWeek dayOfWeek;

        @JsonProperty("start_time")
        @NotNull(message = "시작 시각은 필수입니다.")
        @JsonFormat(pattern = "HH:mm")
        private LocalTime startTime;

        @JsonProperty("end_time")
        @NotNull(message = "종료 시각은 필수입니다.")
        @JsonFormat(pattern = "HH:mm")
        private LocalTime endTime;
    }
}
