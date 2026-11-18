package com.option.refit.domain.chatbot.dto.response;

// 자유 텍스트 입력이 어떤 기능으로 라우팅됐는지 (프론트가 결과 화면을 고르는 기준)
public enum ChatbotQueryType {
    STUDENT,
    RECOMMEND_TIME,
    UNPROPOSED_STUDENTS,
    MONTHLY_STATUS,
    GUIDE
}
