package com.option.refit.domain.classroom.service;

import com.option.refit.domain.classroom.dto.request.ClassRoomJoinRequest;
import com.option.refit.domain.classroom.dto.response.ClassRoomResponse;
import com.option.refit.domain.user.entity.User;

public interface ClassRoomJoinService {

    ClassRoomResponse joinClassroom(
            ClassRoomJoinRequest request,
            User user
    );
}
