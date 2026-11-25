package com.option.refit.domain.chatbot.service;

import com.option.refit.domain.chatbot.dto.response.ChatbotStudentResponse;
import com.option.refit.global.jwt.JwtPrincipal;

import java.util.List;

public interface ChatbotStudentSearchService {

    // 로그인한 선생님 수업의 학생 중 이름에 name이 포함된 학생 검색. 이름이 비어 있으면 400, 결과가 없으면 404
    List<ChatbotStudentResponse> searchStudents(JwtPrincipal principal, String name);

    // 메시지 안에 이름이 들어 있는 학생 조회 (자유 텍스트 라우팅용). 없으면 빈 목록
    List<ChatbotStudentResponse> findMentionedStudents(JwtPrincipal principal, String message);

    // 메시지의 각 단어가 이름 일부로 들어 있는 학생 조회 (예: "김"으로 김철수 검색). 없으면 빈 목록
    List<ChatbotStudentResponse> findStudentsByPartialName(JwtPrincipal principal, String message);
}
