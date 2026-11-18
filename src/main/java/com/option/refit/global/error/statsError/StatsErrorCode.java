package com.option.refit.global.error.statsError;

import com.option.refit.global.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum StatsErrorCode implements ErrorCode {
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "선생님만 이용할 수 있는 기능입니다");

    private final HttpStatus httpStatus;
    private final String message;
}