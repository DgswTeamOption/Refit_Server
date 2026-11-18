package com.option.refit.domain.chatbot.service;

import com.option.refit.domain.chatbot.dto.response.ChatbotMonthlyStatusResponse;
import com.option.refit.global.jwt.JwtPrincipal;

public interface ChatbotMonthlyStatusService {

    // 로그인한 선생님 수업의 이번 달 결석(승인)·보강 건수 집계
    ChatbotMonthlyStatusResponse getMonthlyStatus(JwtPrincipal principal);
}
