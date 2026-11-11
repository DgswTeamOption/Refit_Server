package com.option.refit.domain.classroom.service.impl;

import com.option.refit.domain.classroom.dto.response.ClassRoomHomeResponse;
import com.option.refit.domain.classroom.entity.ClassRoomEntity;
import com.option.refit.domain.classroom.repository.ClassRoomMemberRepository;
import com.option.refit.domain.classroom.repository.ClassRoomRepository;
import com.option.refit.domain.classroom.service.ClassRoomHomeService;
import com.option.refit.domain.file.service.FileUrlResolver;
import com.option.refit.domain.schedule.dto.ScheduleResponse;
import com.option.refit.domain.schedule.service.ScheduleGetService;
import com.option.refit.domain.user.entity.User;
import com.option.refit.global.error.classError.exception.ClassAccessDeniedException;
import com.option.refit.global.error.classError.exception.ClassNotFoundException;
import com.option.refit.global.jwt.JwtPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClassRoomHomeServiceImpl implements ClassRoomHomeService {

    private final ClassRoomRepository classroomRepository;
    private final ClassRoomMemberRepository memberRepository;
    private final ScheduleGetService scheduleGetService;
    private final FileUrlResolver fileUrlResolver;

    @Override
    @Transactional(readOnly = true)
    public ClassRoomHomeResponse getClassroomHome(
            Long classroomId,
            User user,
            int year,
            int month
    ) {
        // 수업 조회
        ClassRoomEntity classroom = classroomRepository
                .findById(classroomId)
                .orElseThrow(ClassNotFoundException::new);

        //수업 조회
        boolean isTeacher = classroom.getTeacher().getId().equals(user.getId());
        boolean isMember = memberRepository
                .existsByClassroom_IdAndUser_IdAndLeftAtIsNull(classroomId, user.getId());

        //수업을 생성한 교사인지 확인
        if (!isTeacher && !isMember) {
            throw new ClassAccessDeniedException();
        }

        //현재 수업에 참여 중인 확생인지 확인
        long memberCount = memberRepository
                .countByClassroom_IdAndLeftAtIsNull(classroomId);

        // schedule 패키지의 기존 월별 조회
        List<ScheduleResponse> calendar =
                scheduleGetService.getMonthlyCalendar(
                        new JwtPrincipal(user.getId(), user.getRole()), classroomId, year, month);

        return ClassRoomHomeResponse.from(
                classroom,
                memberCount,
                fileUrlResolver.resolve(classroom.getBannerImage()),
                calendar,
                isTeacher
        );
    }
}