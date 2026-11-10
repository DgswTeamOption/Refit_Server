package com.option.refit.domain.classroom.service.impl;

import com.option.refit.domain.classroom.dto.response.ClassRoomResponse;
import com.option.refit.domain.classroom.repository.ClassRoomMemberRepository;
import com.option.refit.domain.classroom.repository.ClassRoomRepository;
import com.option.refit.domain.classroom.service.ClassRoomListService;
import com.option.refit.domain.user.entity.Role;
import com.option.refit.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClassRoomListServiceImpl implements ClassRoomListService {

    private final ClassRoomRepository classroomRepository;
    private final ClassRoomMemberRepository memberRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ClassRoomResponse> getClassrooms(User user) {

        // 교사가 생성한 수업 목록 조회
        if (user.getRole() == Role.TEACHER) {
            return classroomRepository
                    .findAllByTeacher_IdOrderByCreatedAtDesc(user.getId())
                    .stream()
                    .map(ClassRoomResponse::from)
                    .toList();
        }

        // 학생이 현재 참여 중인 수업 목록 조회
        return memberRepository
                .findAllByUser_IdAndLeftAtIsNullOrderByJoinedAtDesc(user.getId())
                .stream()
                .map(member ->
                        ClassRoomResponse.from(member.getClassroom())
                )
                .toList();
    }
}
