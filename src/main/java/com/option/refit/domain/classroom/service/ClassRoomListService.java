package com.option.refit.domain.classroom.service;

import com.option.refit.domain.classroom.dto.response.ClassRoomResponse;
import com.option.refit.domain.user.entity.User;

import java.util.List;

// 사용자의 수업 목록 조회 기능을 정의하는 인터페이스
// 교사는 자신이 생성한 수업 목록을 확인하고 학생은 현재 참여 중인 수업 목록을 조회할 수 있도록 구현

public interface ClassRoomListService {

    List<ClassRoomResponse> getClassrooms(User user);
}
