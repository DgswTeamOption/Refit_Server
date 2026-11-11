package com.option.refit.domain.makeup.service;

import com.option.refit.domain.makeup.dto.MakeupAvailabilityResponse;
import com.option.refit.domain.makeup.entity.MakeupApprovalStatus;
import com.option.refit.domain.makeup.entity.MakeupAvailability;
import com.option.refit.domain.makeup.repository.MakeupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 보강 가능 시간에 신청 인원·변경 가능 여부를 붙여 응답으로 변환한다.
// 신청 인원은 대기+승인만 집계한다 (MakeupApprovalStatus.INACTIVE 인 거절·취소 제외).
@Component
@RequiredArgsConstructor
public class MakeupAvailabilityResponseAssembler {

    private static final List<MakeupApprovalStatus> COUNTED =
            List.of(MakeupApprovalStatus.PENDING, MakeupApprovalStatus.APPROVED);

    private final MakeupRepository makeupRepository;

    // 여러 건을 한 번의 집계 쿼리로 변환한다.
    public List<MakeupAvailabilityResponse> toResponses(List<MakeupAvailability> availabilities) {
        if (availabilities.isEmpty()) {
            return List.of();
        }

        List<Long> classIds = availabilities.stream().map(MakeupAvailability::getClassId).distinct().toList();
        Map<Key, long[]> counts = new HashMap<>(); // [대기, 승인]
        for (MakeupRepository.AvailabilityApplyCount row : makeupRepository.countByAvailability(classIds, COUNTED)) {
            long[] c = counts.computeIfAbsent(
                    new Key(row.getClassId(), row.getScheduleId(), row.getMakeupTime()), k -> new long[2]);
            c[row.getApprovalStatus() == MakeupApprovalStatus.PENDING ? 0 : 1] = row.getApplyCount();
        }

        return availabilities.stream().map(a -> {
            long[] c = counts.getOrDefault(
                    new Key(a.getClassId(), a.getScheduleId(), a.getMakeupTime()), new long[2]);
            return new MakeupAvailabilityResponse(a, c[0], c[1]);
        }).toList();
    }

    public MakeupAvailabilityResponse toResponse(MakeupAvailability availability) {
        return toResponses(List.of(availability)).get(0);
    }

    private record Key(Long classId, Long scheduleId, LocalDateTime makeupTime) {
    }
}
