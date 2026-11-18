package com.option.refit.domain.chatbot.service;

import com.option.refit.domain.chatbot.dto.response.ChatbotUnproposedStudentResponse;
import com.option.refit.global.jwt.JwtPrincipal;

import java.util.List;

public interface ChatbotUnproposedStudentService {

    // 승인된 결석은 있지만 같은 수업에 유효한 보강 신청이 하나도 없는 학생 목록 (최근 결석 순). 없으면 빈 목록
    List<ChatbotUnproposedStudentResponse> getUnproposedStudents(JwtPrincipal principal);
}
