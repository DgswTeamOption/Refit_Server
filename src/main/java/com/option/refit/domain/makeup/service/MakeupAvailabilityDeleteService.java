package com.option.refit.domain.makeup.service;

import com.option.refit.global.jwt.JwtPrincipal;

public interface MakeupAvailabilityDeleteService {

    // 등록한 선생님만 보강 가능 시간을 삭제한다. 신청이 있는 시간대는 삭제할 수 없다.
    void deleteAvailability(JwtPrincipal principal, Long availabilityId);
}
