package com.option.refit.domain.attendance.dto;

import com.option.refit.domain.attendance.entity.AttendanceStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 선생님의 출석·결석 처리: status (PRESENT / ABSENT)
@Getter
@NoArgsConstructor
public class AttendanceMarkRequest {

    @NotNull(message = "출석 상태는 필수입니다.")
    private AttendanceStatus status;
}
