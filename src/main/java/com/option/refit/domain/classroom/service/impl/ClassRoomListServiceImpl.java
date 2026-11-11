package com.option.refit.domain.classroom.service.impl;

import com.option.refit.domain.classroom.dto.response.ClassRoomResponse;
import com.option.refit.domain.classroom.repository.ClassRoomMemberRepository;
import com.option.refit.domain.classroom.repository.ClassRoomRepository;
import com.option.refit.domain.classroom.service.ClassRoomListService;
import com.option.refit.domain.file.service.FileUrlResolver;
import com.option.refit.domain.user.entity.Role;
import com.option.refit.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClassRoomListServiceImpl implements ClassRoomListService {

    // 교사가 생성한 수업 정보를 조회하기 위한 Repository
    private final ClassRoomRepository classroomRepository;

    // 학생이 참여 중인 수업 정보를 조회하기 위한 Repository
    private final ClassRoomMemberRepository memberRepository;

    // 배너 이미지 URL 변환
    private final FileUrlResolver fileUrlResolver;

    @Override
    @Transactional(readOnly = true)
    public List<ClassRoomResponse> getClassrooms(User user) {

        // 로그인한 사용자가 교사인지 확인
        if (user.getRole() == Role.TEACHER) {

            // 교사가 생성한 수업 목록을 생성일 기준 내림차순으로 조회
            return classroomRepository
                    .findAllByTeacher_IdOrderByCreatedAtDesc(user.getId())
                    .stream()

                    // 조회한 수업 Entity를 응답 DTO로 변환
                    .map(classroom -> ClassRoomResponse.from(
                            classroom,
                            fileUrlResolver.resolve(classroom.getBannerImage())
                    ))

                    // 변환된 수업 목록을 List로 반환
                    .toList();
        }

        // 학생이 현재 참여 중인 수업 목록을 참여일 기준 내림차순으로 조회
        return memberRepository
                .findAllByUser_IdAndLeftAtIsNullOrderByJoinedAtDesc(user.getId())
                .stream()

                // 수업 참여 정보에서 수업 Entity를 가져와 응답 DTO로 변환
                .map(member ->
                        ClassRoomResponse.from(
                                member.getClassroom(),
                                fileUrlResolver.resolve(member.getClassroom().getBannerImage())
                        )
                )

                // 변환된 수업 목록을 List로 반환
                .toList();
    }
}
