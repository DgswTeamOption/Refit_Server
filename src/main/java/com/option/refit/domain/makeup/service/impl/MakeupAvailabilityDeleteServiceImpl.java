package com.option.refit.domain.makeup.service.impl;

import com.option.refit.domain.makeup.entity.MakeupApprovalStatus;
import com.option.refit.domain.makeup.entity.MakeupAvailability;
import com.option.refit.domain.makeup.repository.MakeupAvailabilityRepository;
import com.option.refit.domain.makeup.repository.MakeupRepository;
import com.option.refit.domain.makeup.service.MakeupAvailabilityDeleteService;
import com.option.refit.domain.user.entity.Role;
import com.option.refit.global.error.makeupError.exception.MakeupAvailabilityAccessDeniedException;
import com.option.refit.global.error.makeupError.exception.MakeupAvailabilityInUseException;
import com.option.refit.global.error.makeupError.exception.MakeupAvailabilityNotFoundException;
import com.option.refit.global.error.securityError.exception.RoleAccessDeniedException;
import com.option.refit.global.jwt.JwtPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MakeupAvailabilityDeleteServiceImpl implements MakeupAvailabilityDeleteService {

    private final MakeupAvailabilityRepository availabilityRepository;
    private final MakeupRepository makeupRepository;

    @Override
    @Transactional
    public void deleteAvailability(JwtPrincipal principal, Long availabilityId) {
        // 선생님만 삭제 가능 (403)
        if (principal.role() != Role.TEACHER) {
            throw new RoleAccessDeniedException();
        }

        // 존재하지 않는 보강 가능 시간 (404). 행을 잠가 동시 신청과 엇갈리지 않게 한다.
        MakeupAvailability availability = availabilityRepository.findByIdForUpdate(availabilityId)
                .orElseThrow(MakeupAvailabilityNotFoundException::new);

        // 등록한 담당 선생님만 삭제 가능 (403)
        if (!availability.getTeacherId().equals(principal.userId())) {
            throw new MakeupAvailabilityAccessDeniedException();
        }

        // 대기·승인 신청이 있으면 신청 건이 갈 곳을 잃으므로 삭제 불가 (409)
        long applied = makeupRepository.countByClassIdAndScheduleIdAndMakeupTimeAndApprovalStatusNotIn(
                availability.getClassId(), availability.getScheduleId(),
                availability.getMakeupTime(), MakeupApprovalStatus.INACTIVE);
        if (applied > 0) {
            throw new MakeupAvailabilityInUseException();
        }

        availabilityRepository.delete(availability);
    }
}
