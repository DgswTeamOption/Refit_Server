package com.option.refit.domain.classroom.service;

import com.option.refit.domain.classroom.dto.request.ClassRoomCreateRequest;
import com.option.refit.domain.classroom.dto.response.ClassRoomResponse;
import com.option.refit.domain.user.entity.User;

// 교사 권환을 확인한 다음에 수업을 생성한다.

public interface ClassRoomCreateService {

    ClassRoomResponse createClassroom(
            ClassRoomCreateRequest request,
            User user
    );
}
