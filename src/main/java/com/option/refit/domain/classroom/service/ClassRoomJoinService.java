package com.option.refit.domain.classroom.service;

import com.option.refit.domain.classroom.dto.request.ClassRoomJoinRequest;
import com.option.refit.domain.classroom.dto.response.ClassRoomResponse;
import com.option.refit.domain.user.entity.User;

// 수업 참여 기능을 정의하는 인터페이스
// 학생이 올바른 수업 참여 코드를 입력하면 해당 수업에 참여할 수 있도록 구현
public interface ClassRoomJoinService {



    ClassRoomResponse joinClassroom(
            ClassRoomJoinRequest request,
            User user
    );
}
