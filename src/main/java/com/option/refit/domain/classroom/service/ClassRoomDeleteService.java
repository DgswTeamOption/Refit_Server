package com.option.refit.domain.classroom.service;

import com.option.refit.domain.user.entity.User;

// 수업 삭제 기능을 정의하는 인터페이스
// 담당 교사만 자신의 수업과 연관 데이터를 삭제할 수 있다.

public interface ClassRoomDeleteService {

    void deleteClassroom(Long classroomId, User user);
}
