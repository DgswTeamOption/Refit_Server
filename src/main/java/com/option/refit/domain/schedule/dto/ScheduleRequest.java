package com.option.refit.domain.schedule.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class ScheduleRequest {

    // 일정 등록: student_id, start_time, end_time
    @Getter
    @NoArgsConstructor
    public static class Create {
        @JsonProperty("student_id")
        @NotNull(message = "학생은 필수입니다.")
        private Long studentId;

        @JsonProperty("start_time")
        @NotNull(message = "시작 시각은 필수입니다.")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime startTime;

        @JsonProperty("end_time")
        @NotNull(message = "종료 시각은 필수입니다.")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime endTime;
    }

    // 일정 수정: start_time, end_time
    @Getter
    @NoArgsConstructor
    public static class Update {
        @JsonProperty("start_time")
        @NotNull(message = "시작 시각은 필수입니다.")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime startTime;

        @JsonProperty("end_time")
        @NotNull(message = "종료 시각은 필수입니다.")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime endTime;
    }
}
