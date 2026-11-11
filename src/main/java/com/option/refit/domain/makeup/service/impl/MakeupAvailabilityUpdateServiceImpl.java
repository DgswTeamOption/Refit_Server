package com.option.refit.domain.makeup.service.impl;

import com.option.refit.domain.makeup.dto.MakeupAvailabilityResponse;
import com.option.refit.domain.makeup.dto.MakeupAvailabilityUpdateRequest;
import com.option.refit.domain.makeup.entity.MakeupApprovalStatus;
import com.option.refit.domain.makeup.entity.MakeupAvailability;
import com.option.refit.domain.makeup.repository.MakeupAvailabilityRepository;
import com.option.refit.domain.makeup.repository.MakeupRepository;
import com.option.refit.domain.makeup.service.MakeupAvailabilityResponseAssembler;
import com.option.refit.domain.makeup.service.MakeupAvailabilityUpdateService;
import com.option.refit.domain.user.entity.Role;
import com.option.refit.global.error.makeupError.exception.DuplicateMakeupAvailabilityException;
import com.option.refit.global.error.makeupError.exception.InvalidDateRangeException;
import com.option.refit.global.error.makeupError.exception.MakeupAvailabilityAccessDeniedException;
import com.option.refit.global.error.makeupError.exception.MakeupAvailabilityInUseException;
import com.option.refit.global.error.makeupError.exception.MakeupAvailabilityNotFoundException;
import com.option.refit.global.error.makeupError.exception.MakeupCapacityBelowAppliedException;
import com.option.refit.global.error.securityError.exception.RoleAccessDeniedException;
import com.option.refit.global.jwt.JwtPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MakeupAvailabilityUpdateServiceImpl implements MakeupAvailabilityUpdateService {

    private final MakeupAvailabilityRepository availabilityRepository;
    private final MakeupRepository makeupRepository;
    private final MakeupAvailabilityResponseAssembler assembler;

    @Override
    @Transactional
    public MakeupAvailabilityResponse updateAvailability(
            JwtPrincipal principal, Long availabilityId, MakeupAvailabilityUpdateRequest request) {
        // 선생님만 수정 가능 (403)
        if (principal.role() != Role.TEACHER) {
            throw new RoleAccessDeniedException();
        }

        // 존재하지 않는 보강 가능 시간 (404). 행을 잠가 동시 신청과 엇갈리지 않게 한다.
        MakeupAvailability availability = availabilityRepository.findByIdForUpdate(availabilityId)
                .orElseThrow(MakeupAvailabilityNotFoundException::new);

        // 등록한 담당 선생님만 수정 가능 (403)
        if (!availability.getTeacherId().equals(principal.userId())) {
            throw new MakeupAvailabilityAccessDeniedException();
        }

        // 현재 이 시간대에 걸린 유효 신청(대기+승인) 수
        long applied = makeupRepository.countByClassIdAndScheduleIdAndMakeupTimeAndApprovalStatusNotIn(
                availability.getClassId(), availability.getScheduleId(),
                availability.getMakeupTime(), MakeupApprovalStatus.INACTIVE);

        // 검증을 모두 마친 뒤 변경한다 (일부만 반영되는 것 방지)
        LocalDateTime newTime = request.getMakeupTime();
        boolean timeChanged = newTime != null && !newTime.equals(availability.getMakeupTime());
        if (timeChanged) {
            // 과거 시간으로 변경 불가 (400)
            if (newTime.isBefore(LocalDateTime.now())) {
                throw new InvalidDateRangeException();
            }
            // 같은 수업 일정에 같은 시간이 이미 등록돼 있으면 겹침 (409)
            if (availabilityRepository.existsByClassIdAndScheduleIdAndMakeupTimeAndIdNot(
                    availability.getClassId(), availability.getScheduleId(), newTime, availability.getId())) {
                throw new DuplicateMakeupAvailabilityException();
            }
            // 이미 신청이 있으면 신청 건의 시간과 어긋나므로 변경 불가 (409)
            if (applied > 0) {
                throw new MakeupAvailabilityInUseException();
            }
        }

        // 정원은 이미 신청된 인원보다 줄일 수 없다 (409)
        Integer newCapacity = request.getCapacity();
        if (newCapacity != null && newCapacity < applied) {
            throw new MakeupCapacityBelowAppliedException();
        }

        if (timeChanged) {
            availability.changeMakeupTime(newTime);
        }
        if (newCapacity != null) {
            availability.changeCapacity(newCapacity);
        }

        return assembler.toResponse(availability);
    }
}
