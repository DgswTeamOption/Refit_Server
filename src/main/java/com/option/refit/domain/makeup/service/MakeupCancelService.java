package com.option.refit.domain.makeup.service;

import com.option.refit.domain.makeup.dto.MakeupApproveResponse;
import com.option.refit.global.jwt.JwtPrincipal;

public interface MakeupCancelService {

    // 신청한 학생 본인이 대기 중인 보강 신청을 취소한다. 담당 선생님에게 알림을 보낸다.
    MakeupApproveResponse cancelMakeup(JwtPrincipal principal, Long makeupId);
}
