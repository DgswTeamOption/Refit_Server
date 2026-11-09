package com.option.refit.domain.classroom.service;

import com.option.refit.domain.classroom.dto.response.ClassRoomHomeResponse;
import com.option.refit.domain.user.entity.User;

// 해당 수업의 교사이거나 참여 학생만 조회 가능

public interface ClassRoomHomeService {

    ClassRoomHomeResponse getClassroomHome(
            Long classroomId,
            User user,
            int year,
            int month
    );
}
