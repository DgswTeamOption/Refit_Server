package com.option.refit.domain.makeup.service.impl;

import com.option.refit.domain.makeup.dto.MakeupAvailabilityRequest;
import com.option.refit.domain.makeup.dto.MakeupAvailabilityResponse;
import com.option.refit.domain.makeup.entity.MakeupAvailability;
import com.option.refit.domain.makeup.repository.MakeupAvailabilityRepository;
import com.option.refit.domain.makeup.service.MakeupAvailabilityCreateService;
import com.option.refit.domain.schedule.repository.ScheduleRepository;
import com.option.refit.domain.user.entity.Role;
import com.option.refit.global.error.makeupError.exception.DuplicateMakeupAvailabilityException;
import com.option.refit.global.error.makeupError.exception.InvalidDateRangeException;
import com.option.refit.global.error.scheduleError.exception.ScheduleNotFoundException;
import com.option.refit.global.error.securityError.exception.RoleAccessDeniedException;
import com.option.refit.global.jwt.JwtPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MakeupAvailabilityCreateServiceImpl implements MakeupAvailabilityCreateService {

    private final MakeupAvailabilityRepository availabilityRepository;
    private final ScheduleRepository scheduleRepository;

    // TODO: 수업 도메인 머지 후 "본인이 담당하는 수업인지" 검사 추가
    @Override
    @Transactional
    public List<MakeupAvailabilityResponse> createAvailabilities(JwtPrincipal principal, MakeupAvailabilityRequest request) {
        // 선생님만 등록 가능 (403)
        if (principal.role() != Role.TEACHER) {
            throw new RoleAccessDeniedException();
        }

        // 존재하지 않는 수업 / 일정 (404)
        scheduleRepository.findByIdAndClassId(request.getScheduleId(), request.getClassId())
                .orElseThrow(ScheduleNotFoundException::new);

        // 전체 검증을 먼저 하고 나서 저장 (일부만 저장되는 것 방지)
        LocalDateTime now = LocalDateTime.now();
        Set<LocalDateTime> requested = new HashSet<>();
        for (LocalDateTime makeupTime : request.getMakeupTimes()) {
            if (makeupTime.isBefore(now)) {
                throw new InvalidDateRangeException(); // 지난 시간 (400)
            }
            boolean duplicatedInRequest = !requested.add(makeupTime);
            if (duplicatedInRequest || availabilityRepository.existsByClassIdAndScheduleIdAndMakeupTime(
                    request.getClassId(), request.getScheduleId(), makeupTime)) {
                throw new DuplicateMakeupAvailabilityException(); // 중복 등록 (409)
            }
        }

        List<MakeupAvailability> saved = availabilityRepository.saveAll(
                request.getMakeupTimes().stream()
                        .map(makeupTime -> MakeupAvailability.builder()
                                .classId(request.getClassId())
                                .teacherId(principal.userId())
                                .scheduleId(request.getScheduleId())
                                .makeupTime(makeupTime)
                                .capacity(request.getCapacityOrDefault())
                                .build())
                        .toList());

        return saved.stream().map(MakeupAvailabilityResponse::new).toList();
    }
}
