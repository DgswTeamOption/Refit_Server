package com.option.refit.domain.makeup.service;

import com.option.refit.domain.makeup.dto.MakeupAvailabilityRequest;
import com.option.refit.domain.makeup.dto.MakeupAvailabilityResponse;
import com.option.refit.global.jwt.JwtPrincipal;

import java.util.List;

public interface MakeupAvailabilityCreateService {
    List<MakeupAvailabilityResponse> createAvailabilities(JwtPrincipal principal, MakeupAvailabilityRequest request);
}
