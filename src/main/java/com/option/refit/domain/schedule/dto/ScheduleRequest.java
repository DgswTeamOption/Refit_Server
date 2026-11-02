package com.option.refit.domain.schedule.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class ScheduleRequest {

    @Getter
    @NoArgsConstructor
    public static class CreateOrUpdate {
        @NotBlank(message = "수업/일정 명은 필수입니다.")
        @Size(max = 255, message = "수업/일정 명은 255자 이하여야 합니다.")
        private String className;

        private String description;

        @NotNull(message = "일정 시각은 필수입니다.")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime scheduleTime;
    }
}
