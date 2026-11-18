package com.option.refit.global.error.chatbotError;

import com.option.refit.global.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ChatbotErrorCode implements ErrorCode {
    INVALID_SEARCH_NAME(HttpStatus.BAD_REQUEST, "검색할 학생 이름을 입력해주세요"),
    STUDENT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 이름의 학생을 찾을 수 없습니다"),
    NO_RECOMMENDATION_DATA(HttpStatus.NOT_FOUND, "추천할 보강 신청 데이터가 없습니다");

    private final HttpStatus httpStatus;
    private final String message;
}
