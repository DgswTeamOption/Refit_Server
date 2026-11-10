package com.option.refit.domain.absence.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.option.refit.domain.absence.entity.Absence;
import com.option.refit.domain.absence.entity.AbsenceStatus;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

// 목록 조회용 응답: AbsenceResponse + 학생 이름
@Getter
public class AbsenceListResponse {

    private final Long id;

    @JsonProperty("class_id")
    private final Long classId;

    @JsonProperty("student_id")
    private final Long studentId;

    @JsonProperty("student_name")
    private final String studentName;

    @JsonProperty("absence_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private final LocalDate absenceDate;

    private final String reason;

    private final AbsenceStatus status;

    @JsonProperty("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime createdAt;

    @JsonProperty("updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime updatedAt;

    public AbsenceListResponse(Absence absence, String studentName) {
        this.id = absence.getId();
        this.classId = absence.getClassId();
        this.studentId = absence.getStudentId();
        this.studentName = studentName;
        this.absenceDate = absence.getAbsenceDate();
        this.reason = absence.getReason();
        this.status = absence.getStatus();
        this.createdAt = absence.getCreatedAt();
        this.updatedAt = absence.getUpdatedAt();
    }
}
