package com.option.refit.domain.makeup.service.impl;

import com.option.refit.domain.makeup.dto.MakeupAvailabilityResponse;
import com.option.refit.domain.makeup.repository.MakeupAvailabilityRepository;
import com.option.refit.domain.makeup.service.MakeupAvailabilityGetService;
import com.option.refit.global.error.makeupError.exception.MethodArgumentNotValidException;
import com.option.refit.global.jwt.JwtPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MakeupAvailabilityGetServiceImpl implements MakeupAvailabilityGetService {

    private final MakeupAvailabilityRepository availabilityRepository;

    // TODO: 기존 보강 조회와 동일하게 수업 소속 여부 검사는 수업 도메인 연동 시 추가
    @Override
    public List<MakeupAvailabilityResponse> getAvailabilities(JwtPrincipal principal, Long classId, Long teacherId) {
        // 조건 없는 전체 조회는 허용하지 않는다 (400)
        if (classId == null && teacherId == null) {
            throw new MethodArgumentNotValidException();
        }

        return availabilityRepository.findList(classId, teacherId).stream()
                .map(MakeupAvailabilityResponse::new)
                .toList();
    }
}
