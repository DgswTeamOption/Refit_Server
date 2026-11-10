package com.option.refit.domain.makeup.service;

import com.option.refit.domain.makeup.dto.MakeupRequest;
import com.option.refit.domain.makeup.dto.MakeupResponse;
import com.option.refit.global.jwt.JwtPrincipal;

public interface MakeupRequestService {
    MakeupResponse requestMakeup(JwtPrincipal principal, MakeupRequest request);
}
