package com.option.refit.domain.classroom.service;

import com.option.refit.domain.classroom.dto.response.ClassRoomStudentResponse;
import com.option.refit.domain.user.entity.User;

import java.util.List;

// 수업에 참여 중인 학생 목록 조회 기능을 정의하는 인터페이스
// 해당 수업을 생성한 교사만 조회할 수 있도록 구현
public interface ClassRoomStudentListService {

    List<ClassRoomStudentResponse> getStudents(Long classroomId, User user);
}
