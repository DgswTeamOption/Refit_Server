
package com.option.refit.domain.classroom.service;

import com.option.refit.domain.classroom.dto.response.ClassRoomHomeResponse;
import com.option.refit.domain.user.entity.User;

public interface ClassRoomHomeService {

    ClassRoomHomeResponse getClassroomHome(
            Long classroomId,
            User user
    );
}
