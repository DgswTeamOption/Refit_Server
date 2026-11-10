package com.option.refit.domain.classroom.service;

import com.option.refit.domain.user.entity.User;

// 수업 나가기 기능을 정의하는 인터페이스
// 현재 수업에 참여 중인 학생이 해당 수업에서 나갈 수 있도록 구현

public interface ClassRoomLeaveService {

    void leaveClassroom(Long classroomId, User user);
}
