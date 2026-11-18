package com.option.refit.domain.chatbot.service;

import com.option.refit.domain.chatbot.dto.response.ChatbotRecommendedTimeResponse;
import com.option.refit.global.jwt.JwtPrincipal;

import java.util.List;

public interface ChatbotRecommendTimeService {

    // 신청자 수 기준 보강 시간대 Top3. classId가 null이면 선생님의 전체 수업 기준,
    // 있으면 본인 수업이어야 한다(없는 수업 404, 남의 수업 403). 집계할 신청이 없으면 404
    List<ChatbotRecommendedTimeResponse> recommend(JwtPrincipal principal, Long classId);
}
