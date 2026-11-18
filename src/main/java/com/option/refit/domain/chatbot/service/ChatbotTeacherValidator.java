package com.option.refit.domain.chatbot.service;

import com.option.refit.domain.user.entity.Role;
import com.option.refit.global.error.securityError.exception.RoleAccessDeniedException;
import com.option.refit.global.jwt.JwtPrincipal;
import org.springframework.stereotype.Component;

// 챗봇은 선생님 전용. 학생 토큰이면 403
@Component
public class ChatbotTeacherValidator {

    public void requireTeacher(JwtPrincipal principal) {
        if (principal.role() != Role.TEACHER) {
            throw new RoleAccessDeniedException();
        }
    }
}
