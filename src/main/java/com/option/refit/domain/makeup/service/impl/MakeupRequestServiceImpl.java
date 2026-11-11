package com.option.refit.domain.makeup.service.impl;

import com.option.refit.domain.makeup.dto.MakeupRequest;
import com.option.refit.domain.makeup.dto.MakeupResponse;
import com.option.refit.domain.makeup.entity.Makeup;
import com.option.refit.domain.makeup.entity.MakeupApprovalStatus;
import com.option.refit.domain.makeup.entity.MakeupAvailability;
import com.option.refit.domain.makeup.repository.MakeupAvailabilityRepository;
import com.option.refit.domain.makeup.repository.MakeupRepository;
import com.option.refit.domain.makeup.service.MakeupRequestService;
import com.option.refit.domain.schedule.entity.ScheduleEntity;
import com.option.refit.domain.schedule.repository.ScheduleRepository;
import com.option.refit.domain.user.entity.Role;
import com.option.refit.global.error.makeupError.exception.DuplicateMakeupException;
import com.option.refit.global.error.makeupError.exception.InvalidDateRangeException;
import com.option.refit.global.error.makeupError.exception.MakeupAvailabilityNotFoundException;
import com.option.refit.global.error.makeupError.exception.MakeupCapacityExceededException;
import com.option.refit.global.error.scheduleError.exception.ScheduleNotFoundException;
import com.option.refit.global.error.securityError.exception.RoleAccessDeniedException;
import com.option.refit.global.jwt.JwtPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MakeupRequestServiceImpl implements MakeupRequestService {

    private final MakeupRepository makeupRepository;
    private final MakeupAvailabilityRepository availabilityRepository;
    private final ScheduleRepository scheduleRepository;

    @Override
    @Transactional
    public MakeupResponse requestMakeup(JwtPrincipal principal, MakeupRequest request) {
        // 학생만 신청 가능 (403)
        if (principal.role() != Role.STUDENT) {
            throw new RoleAccessDeniedException();
        }

        // 존재하지 않는 수업 / 기존 수업 일정 (404)
        ScheduleEntity schedule = scheduleRepository.findByIdAndClassId(request.getScheduleId(), request.getClassId())
                .orElseThrow(ScheduleNotFoundException::new);

        // 본인에게 배정된 일정에 대해서만 신청 가능 (403)
        if (!schedule.getStudentId().equals(principal.userId())) {
            throw new RoleAccessDeniedException();
        }

        // 선생님이 등록한 보강 가능 시간 중 하나여야 함 (404)
        // 행 잠금으로 같은 시간대에 동시에 신청해도 정원을 초과하지 않도록 순차 처리
        MakeupAvailability availability = availabilityRepository
                .findForUpdate(request.getClassId(), request.getScheduleId(), request.getMakeupTime())
                .orElseThrow(MakeupAvailabilityNotFoundException::new);

        // 이미 지난 시간은 신청 불가 (400)
        if (availability.getMakeupTime().isBefore(LocalDateTime.now())) {
            throw new InvalidDateRangeException();
        }

        // 같은 일정에 유효한(거절·취소 제외) 신청이 있으면 중복 (409)
        if (makeupRepository.existsByStudentIdAndClassIdAndScheduleIdAndApprovalStatusNotIn(
                principal.userId(), request.getClassId(), request.getScheduleId(), MakeupApprovalStatus.INACTIVE)) {
            throw new DuplicateMakeupException();
        }

        // 시간대별 정원 초과 (409): 대기+승인 건수가 정원 이상이면 마감
        long applied = makeupRepository.countByClassIdAndScheduleIdAndMakeupTimeAndApprovalStatusNotIn(
                request.getClassId(), request.getScheduleId(), request.getMakeupTime(), MakeupApprovalStatus.INACTIVE);
        if (applied >= availability.getCapacity()) {
            throw new MakeupCapacityExceededException();
        }

        Makeup makeup = Makeup.builder()
                .studentId(principal.userId())
                .teacherId(availability.getTeacherId())
                .classId(request.getClassId())
                .scheduleId(request.getScheduleId())
                .makeupTime(request.getMakeupTime())
                .build();

        return new MakeupResponse(makeupRepository.save(makeup));
    }
}
