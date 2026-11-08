
package com.option.refit.domain.classroom.service;

import com.option.refit.domain.classroom.dto.response.ClassRoomResponse;
import com.option.refit.domain.user.entity.User;

import java.util.List;

public interface ClassRoomListService {

    List<ClassRoomResponse> getClassrooms(User user);
}
