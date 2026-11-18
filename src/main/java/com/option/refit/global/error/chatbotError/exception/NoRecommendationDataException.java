package com.option.refit.global.error.chatbotError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.chatbotError.ChatbotErrorCode;

public class NoRecommendationDataException extends CustomException {
    public NoRecommendationDataException() {
        super(ChatbotErrorCode.NO_RECOMMENDATION_DATA);
    }
}
