package com.option.refit.domain.absence.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

// 결석 신청: class_id, absence_date, reason
@Getter
@NoArgsConstructor
public class AbsenceRequest {

    @JsonProperty("class_id")
    @NotNull(message = "수업은 필수입니다.")
    private Long classId;

    @JsonProperty("absence_date")
    @NotNull(message = "결석 일자는 필수입니다.")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate absenceDate;

    @NotBlank(message = "결석 사유는 필수입니다.")
    @Size(max = 500, message = "결석 사유는 500자 이내로 입력해주세요.")
    private String reason;
}
