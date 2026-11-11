package com.option.refit.domain.makeup.service.impl;

import com.option.refit.domain.classroom.service.ClassRoomAccessValidator;
import com.option.refit.domain.makeup.dto.MakeupAvailabilityResponse;
import com.option.refit.domain.makeup.entity.MakeupAvailability;
import com.option.refit.domain.makeup.repository.MakeupAvailabilityRepository;
import com.option.refit.domain.makeup.service.MakeupAvailabilityGetService;
import com.option.refit.domain.makeup.service.MakeupAvailabilityResponseAssembler;
import com.option.refit.domain.user.entity.Role;
import com.option.refit.global.error.classError.exception.ClassAccessDeniedException;
import com.option.refit.global.error.makeupError.exception.MethodArgumentNotValidException;
import com.option.refit.global.jwt.JwtPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MakeupAvailabilityGetServiceImpl implements MakeupAvailabilityGetService {

    private final MakeupAvailabilityRepository availabilityRepository;
    private final ClassRoomAccessValidator accessValidator;
    private final MakeupAvailabilityResponseAssembler assembler;

    @Override
    public List<MakeupAvailabilityResponse> getAvailabilities(
            JwtPrincipal principal, Long classId, Long teacherId, Long scheduleId) {
        // 조건 없는 전체 조회는 허용하지 않는다 (400)
        if (classId == null && teacherId == null) {
            throw new MethodArgumentNotValidException();
        }

        List<MakeupAvailability> found;
        if (classId != null) {
            // 수업 지정: 수업이 없으면 404, 담당 선생님이나 참여 중인 학생이 아니면 403
            accessValidator.requireTeacherOrMember(classId, principal.userId());
            found = availabilityRepository.findList(classId, teacherId, scheduleId);
        } else if (principal.role() == Role.TEACHER) {
            // 선생님만 지정: 본인 것만 조회 가능 (다른 선생님이면 403)
            if (!teacherId.equals(principal.userId())) {
                throw new ClassAccessDeniedException();
            }
            found = availabilityRepository.findList(null, teacherId, scheduleId);
        } else {
            // 학생이 선생님만 지정: 참여 중인 수업의 데이터만 남긴다
            Map<Long, Boolean> memberOf = new HashMap<>();
            found = availabilityRepository.findList(null, teacherId, scheduleId).stream()
                    .filter(a -> memberOf.computeIfAbsent(
                            a.getClassId(), id -> accessValidator.isActiveMember(id, principal.userId())))
                    .toList();
        }

        return assembler.toResponses(found);
    }
}
