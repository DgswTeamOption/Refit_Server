package com.option.refit.domain.attendance.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.option.refit.domain.attendance.entity.Attendance;
import com.option.refit.domain.attendance.entity.AttendanceMethod;
import com.option.refit.domain.schedule.entity.ScheduleEntity;
import lombok.Getter;

import java.time.LocalDateTime;

// 선생님의 수업별 출석 현황: 일정 한 건당 학생의 출석 상태
// status는 PRESENT / ABSENT, 아직 처리되지 않았으면 NOT_CHECKED
@Getter
public class AttendanceStatusResponse {

    public static final String NOT_CHECKED = "NOT_CHECKED";

    @JsonProperty("schedule_id")
    private final Long scheduleId;

    @JsonProperty("student_id")
    private final Long studentId;

    @JsonProperty("student_name")
    private final String studentName;

    @JsonProperty("start_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime startTime;

    @JsonProperty("end_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime endTime;

    private final String status;

    private final AttendanceMethod method;

    @JsonProperty("checked_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime checkedAt;

    // attendance는 아직 처리되지 않았으면 null
    public AttendanceStatusResponse(ScheduleEntity schedule, String studentName, Attendance attendance) {
        this.scheduleId = schedule.getId();
        this.studentId = schedule.getStudentId();
        this.studentName = studentName;
        this.startTime = schedule.getStartTime();
        this.endTime = schedule.getEndTime();
        this.status = attendance != null ? attendance.getStatus().name() : NOT_CHECKED;
        this.method = attendance != null ? attendance.getMethod() : null;
        this.checkedAt = attendance != null ? attendance.getCheckedAt() : null;
    }
}
