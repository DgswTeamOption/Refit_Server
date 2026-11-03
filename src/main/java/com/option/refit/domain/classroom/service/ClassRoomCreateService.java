package com.option.refit.domain.classroom.service;

import com.option.refit.domain.classroom.dto.request.ClassRoomCreateRequest;
import com.option.refit.domain.classroom.dto.response.ClassRoomResponse;
import com.option.refit.domain.user.entity.User;


public interface ClassRoomCreateService {

    ClassRoomResponse createClassroom(
            ClassRoomCreateRequest request,
            User user
    );
}
