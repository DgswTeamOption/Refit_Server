package com.option.refit.domain.chatbot.dto.response;

// 자유 텍스트 응답. data는 type에 따라 해당 기능 응답(목록 또는 객체), GUIDE이면 null
public record ChatbotQueryResponse(
        ChatbotQueryType type,
        String message,
        Object data
) {}
