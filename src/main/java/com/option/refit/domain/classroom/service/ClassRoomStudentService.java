
package com.option.refit.domain.classroom.service;

import com.option.refit.domain.classroom.dto.response.ClassRoomStudentResponse;

import java.util.List;

public interface ClassRoomStudentService {

    List<ClassRoomStudentResponse> getStudents(Long classId);
}
