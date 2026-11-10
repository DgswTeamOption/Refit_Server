package com.option.refit.domain.makeup.service;

import com.option.refit.domain.makeup.dto.MakeupApproveRequest;
import com.option.refit.domain.makeup.dto.MakeupApproveResponse;
import com.option.refit.global.jwt.JwtPrincipal;

public interface MakeupApproveService {
    MakeupApproveResponse processMakeup(JwtPrincipal principal, Long makeupId, MakeupApproveRequest request);
}
