package com.option.refit.domain.classroom.service;

import com.option.refit.domain.classroom.dto.request.ClassRoomBannerChangeRequest;
import com.option.refit.domain.classroom.dto.response.ClassRoomResponse;
import com.option.refit.domain.user.entity.User;

// 담당 교사만 수업 배너를 교체·삭제한다. 기존 배너 파일은 함께 정리한다.
public interface ClassRoomBannerService {

    ClassRoomResponse changeBanner(Long classroomId, ClassRoomBannerChangeRequest request, User user);

    void deleteBanner(Long classroomId, User user);
}
