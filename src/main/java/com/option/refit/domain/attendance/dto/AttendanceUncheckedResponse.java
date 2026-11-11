package com.option.refit.domain.attendance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

// 수강생 중 출석·지각 기록이 없는 학생. status는 ABSENT(결석 처리됨) 또는 NOT_CHECKED(기록 없음)
@Getter
public class AttendanceUncheckedResponse {

    @JsonProperty("student_id")
    private final Long studentId;

    @JsonProperty("student_name")
    private final String studentName;

    private final String status;

    // 해당 날짜에 이 학생의 수업 일정이 있는지
    @JsonProperty("has_schedule")
    private final boolean hasSchedule;

    public AttendanceUncheckedResponse(Long studentId, String studentName, String status, boolean hasSchedule) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.status = status;
        this.hasSchedule = hasSchedule;
    }
}
