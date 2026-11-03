package com.option.refit.domain.classroom.service;

import com.option.refit.domain.user.entity.User;

public interface ClassRoomLeaveService {

    void leaveClassroom(Long classroomId, User user);
}
