package com.option.refit.domain.attendance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

// 월별 출석·지각·결석 집계 (학생 한 명 기준)
@Getter
public class AttendanceSummaryResponse {

    @JsonProperty("student_id")
    private final Long studentId;

    @JsonProperty("student_name")
    private final String studentName;

    private final int year;

    private final int month;

    @JsonProperty("present_count")
    private final long presentCount;

    @JsonProperty("late_count")
    private final long lateCount;

    @JsonProperty("absent_count")
    private final long absentCount;

    public AttendanceSummaryResponse(Long studentId, String studentName, int year, int month,
                                     long presentCount, long lateCount, long absentCount) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.year = year;
        this.month = month;
        this.presentCount = presentCount;
        this.lateCount = lateCount;
        this.absentCount = absentCount;
    }
}
