package com.option.refit.global.error.chatbotError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.chatbotError.ChatbotErrorCode;

public class ChatbotStudentNotFoundException extends CustomException {
    public ChatbotStudentNotFoundException() {
        super(ChatbotErrorCode.STUDENT_NOT_FOUND);
    }
}
