package com.option.refit.domain.makeup.service;

import com.option.refit.domain.makeup.dto.MakeupAvailabilityResponse;
import com.option.refit.domain.makeup.dto.MakeupAvailabilityUpdateRequest;
import com.option.refit.global.jwt.JwtPrincipal;

public interface MakeupAvailabilityUpdateService {

    // 등록한 선생님만 보강 가능 시간(시간·정원)을 수정한다.
    MakeupAvailabilityResponse updateAvailability(
            JwtPrincipal principal, Long availabilityId, MakeupAvailabilityUpdateRequest request);
}
