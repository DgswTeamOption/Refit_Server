package com.option.refit.domain.chatbot.service;

import com.option.refit.domain.chatbot.dto.request.ChatbotQueryRequest;
import com.option.refit.domain.chatbot.dto.response.ChatbotQueryResponse;
import com.option.refit.global.jwt.JwtPrincipal;

public interface ChatbotQueryService {

    // 자유 텍스트를 규칙 기반으로 각 기능에 라우팅 (학생 이름 > 추천 > 미제안 > 이번달/현황 > 안내 순)
    ChatbotQueryResponse query(JwtPrincipal principal, ChatbotQueryRequest request);
}
