
package com.option.refit.domain.classroom.service;

import com.option.refit.domain.classroom.dto.response.ClassRoomHomeResponse;
import com.option.refit.domain.user.entity.User;

// 수업 홈은 해당 수업의 교사이거나 현재 참여 중인 학생만 조회 가능하게 구현

public interface ClassRoomHomeService {

    ClassRoomHomeResponse getClassroomHome(
            Long classroomId,
            User user
    );
}
