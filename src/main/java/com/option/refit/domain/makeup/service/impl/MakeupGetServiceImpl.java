package com.option.refit.domain.makeup.service.impl;

import com.option.refit.domain.makeup.dto.MakeupResponse;
import com.option.refit.domain.makeup.dto.MakeupScheduleResponse;
import com.option.refit.domain.makeup.entity.MakeupApprovalStatus;
import com.option.refit.domain.makeup.repository.MakeupRepository;
import com.option.refit.domain.makeup.service.MakeupGetService;
import com.option.refit.domain.user.entity.Role;
import com.option.refit.global.error.makeupError.exception.InvalidApprovalStatusException;
import com.option.refit.global.error.scheduleError.exception.InvalidDateFormatException;
import com.option.refit.global.jwt.JwtPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MakeupGetServiceImpl implements MakeupGetService {

    private final MakeupRepository makeupRepository;

    // 선생님은 수업 전체, 학생은 본인 신청 건만 조회
    // TODO: 수업 도메인 머지 후 수업 소속 여부 검사 추가
    @Override
    public List<MakeupResponse> getMakeups(JwtPrincipal principal, Long classId, Integer approvalStatus) {
        MakeupApprovalStatus status = toStatus(approvalStatus);

        return makeupRepository.findList(classId, status, visibleStudentId(principal, null))
                .stream()
                .map(MakeupResponse::new)
                .toList();
    }

    @Override
    public List<MakeupScheduleResponse> getMakeupSchedules(
            JwtPrincipal principal, Long classId, Long studentId, Long scheduleId, String makeupTime) {
        LocalDateTime time = toDateTime(makeupTime);

        return makeupRepository.findSchedules(classId, visibleStudentId(principal, studentId), scheduleId, time)
                .stream()
                .map(MakeupScheduleResponse::new)
                .toList();
    }

    // 학생은 요청한 student_id와 상관없이 항상 본인 것만 보이도록 고정
    private Long visibleStudentId(JwtPrincipal principal, Long requestedStudentId) {
        return principal.role() == Role.STUDENT ? principal.userId() : requestedStudentId;
    }

    // null이면 필터 없음, 0~2 범위 밖이면 400
    private MakeupApprovalStatus toStatus(Integer approvalStatus) {
        if (approvalStatus == null) {
            return null;
        }
        MakeupApprovalStatus[] values = MakeupApprovalStatus.values();
        if (approvalStatus < 0 || approvalStatus >= values.length) {
            throw new InvalidApprovalStatusException();
        }
        return values[approvalStatus];
    }

    // 형식: 2026-10-25T14:00:00, 잘못된 형식이면 400 (쿼리 파라미터 타입 오류는 500으로 빠지므로 직접 파싱)
    private LocalDateTime toDateTime(String makeupTime) {
        if (makeupTime == null || makeupTime.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(makeupTime);
        } catch (DateTimeParseException e) {
            throw new InvalidDateFormatException();
        }
    }
}
