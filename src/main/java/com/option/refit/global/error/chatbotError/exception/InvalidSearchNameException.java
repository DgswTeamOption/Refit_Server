package com.option.refit.global.error.chatbotError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.chatbotError.ChatbotErrorCode;

public class InvalidSearchNameException extends CustomException {
    public InvalidSearchNameException() {
        super(ChatbotErrorCode.INVALID_SEARCH_NAME);
    }
}
