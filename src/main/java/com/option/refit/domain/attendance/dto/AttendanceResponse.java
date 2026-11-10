package com.option.refit.domain.attendance.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.option.refit.domain.attendance.entity.Attendance;
import com.option.refit.domain.attendance.entity.AttendanceMethod;
import com.option.refit.domain.attendance.entity.AttendanceStatus;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

// 출석 기록 한 건
@Getter
public class AttendanceResponse {

    private final Long id;

    @JsonProperty("class_id")
    private final Long classId;

    @JsonProperty("schedule_id")
    private final Long scheduleId;

    @JsonProperty("student_id")
    private final Long studentId;

    @JsonProperty("attendance_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private final LocalDate attendanceDate;

    private final AttendanceStatus status;

    private final AttendanceMethod method;

    @JsonProperty("checked_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime checkedAt;

    public AttendanceResponse(Attendance attendance) {
        this.id = attendance.getId();
        this.classId = attendance.getClassId();
        this.scheduleId = attendance.getScheduleId();
        this.studentId = attendance.getStudentId();
        this.attendanceDate = attendance.getAttendanceDate();
        this.status = attendance.getStatus();
        this.method = attendance.getMethod();
        this.checkedAt = attendance.getCheckedAt();
    }
}
